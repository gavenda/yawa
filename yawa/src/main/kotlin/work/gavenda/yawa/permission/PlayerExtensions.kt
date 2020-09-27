/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (C) 2020 Gavenda <gavenda@disroot.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package work.gavenda.yawa.permission

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.permissions.PermissionAttachment
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.Yawa
import work.gavenda.yawa.api.bukkitAsyncTask
import work.gavenda.yawa.logger

const val META_PLAYER_PERMISSION_ATTACHMENT = "PlayerPermissionAttachment"

/**
 * Returns the attached permission from this plugin.
 */
var Player.permissionAttachment: PermissionAttachment?
    get() = if (hasMetadata(META_PLAYER_PERMISSION_ATTACHMENT)) {
        getMetadata(META_PLAYER_PERMISSION_ATTACHMENT)
            .first { it.owningPlugin == Yawa.Instance }
            .value() as PermissionAttachment
    } else null
    set(value) {
        if (hasMetadata(META_PLAYER_PERMISSION_ATTACHMENT)) return
        setMetadata(META_PLAYER_PERMISSION_ATTACHMENT, FixedMetadataValue(Yawa.Instance, value))
    }

/**
 * Removes the assigned player attachment from this object
 */
fun Player.removeAttachment() {
    val attachment = permissionAttachment ?: return

    removeAttachment(attachment)
    permissionAttachment = null
}

/**
 * Calculate player permissions.
 */
fun Player.calculatePermissions() = bukkitAsyncTask(Yawa.Instance) {
    val attachment = permissionAttachment

    if (attachment == null) {
        logger.warn("Cannot calculate permissions, permission attachment is not set")
        return@bukkitAsyncTask
    }

    transaction {
        val permissionList = Bukkit.getServer().pluginManager.plugins
            .flatMap { it.description.permissions }
            .map { it.name }

        // Grab group permissions first
        val groupPermissions = GroupPlayer
            .find { GroupPlayerSchema.player eq uniqueId }
            .map { it.group }
            .flatMap { it.permissions }
            // Support asterisk permissions
            .map { groupPerms ->
                if (groupPerms.permission.contains('*')) {
                    val lookupPerm = groupPerms.permission.dropLast(2)
                    permissionList.filter { it.startsWith(lookupPerm) }
                        .map { it to groupPerms.enabled }
                } else listOf(groupPerms.permission to groupPerms.enabled)
            }
            .flatten()
            .toMap()

        // Then player permissions
        val playerPermissions = PlayerPermission
            .find { PlayerPermissionSchema.player eq uniqueId }
            // Support asterisk permissions
            .map { playerPerms ->
                if (playerPerms.permission.contains('*')) {
                    val lookupPerm = playerPerms.permission.dropLast(2)
                    permissionList.filter { it.startsWith(lookupPerm) }
                        .map { it to playerPerms.enabled }
                } else listOf(playerPerms.permission to playerPerms.enabled)
            }
            .flatten()
            .toMap()

        // Apply in order
        attachment.removeAll()
        attachment.setPermissionsFromMap(groupPermissions)
        attachment.setPermissionsFromMap(playerPermissions)
        // Recalculate
        recalculatePermissions()
        updateCommands()
    }
}