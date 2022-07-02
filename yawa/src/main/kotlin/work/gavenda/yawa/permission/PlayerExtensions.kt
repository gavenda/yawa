/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (c) 2022 Gavenda <gavenda@disroot.org>
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
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.logger
import work.gavenda.yawa.plugin
import work.gavenda.yawa.scheduler

/**
 * Removes the assigned player attachment from this object
 */
fun Player.removeAttachment() {
    val attachment = PermissionFeature.attachmentFor(uniqueId)

    if (attachment == null) {
        logger.warn("Cannot remove permissions, permission attachment is not set")
        return
    }

    removeAttachment(attachment)
}

/**
 * Calculate player permissions.
 */
fun Player.calculatePermissions() {
    val attachment = PermissionFeature.attachmentFor(uniqueId)

    if (attachment == null) {
        logger.warn("Cannot calculate permissions, permission attachment is not set")
        return
    }

    scheduler.runTaskAsynchronously(plugin) { _ ->
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

            logger.info("Effective permissions: ${attachment.permissions}")

            // Recalculate
            scheduler.runTask(plugin) { _ ->
                recalculatePermissions()
                updateCommands()
            }
        }
    }
}