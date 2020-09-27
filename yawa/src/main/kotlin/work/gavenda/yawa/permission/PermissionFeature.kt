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

import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionAttachment
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.*
import java.util.*

object PermissionFeature : PluginFeature {
    override val isDisabled get() = Config.Permission.Disabled

    private val permissionAttachments = mutableMapOf<UUID, PermissionAttachment>()
    private val permissionListener = PermissionListener()
    private val permissionCommand = PermissionCommand().apply {
        sub(PermissionPlayerCommand(), "player")
        sub(PermissionGroupCommand(), "group")
    }

    fun attachTo(player: Player) {
        permissionAttachments[player.uniqueId] = player.addAttachment(plugin)
    }
    fun attachmentFor(uuid: UUID) = permissionAttachments[uuid]

    override fun enable() {
        logger.warn("Permissions feature is enabled, please use LuckPerms if you're going for scale")

        super.enable()

        permissionAttachments.clear()

        // In-case of reload lol
        server.onlinePlayers.forEach {
            attachTo(it)
            it.calculatePermissions()
        }
    }

    override fun disable() {
        super.disable()

        server.onlinePlayers.forEach {
            it.removeAttachment()
        }
    }

    override fun enableCommands() {
        plugin.getCommand(Command.PERMISSION)?.setExecutor(permissionCommand)
    }

    override fun disableCommands() {
        plugin.getCommand(Command.PERMISSION)?.setExecutor(DisabledCommand)
    }

    override fun registerEventListeners() {
        pluginManager.registerEvents(permissionListener)
        pluginManager.registerEvents(permissionCommand)
    }

    override fun unregisterEventListeners() {
        pluginManager.unregisterEvents(permissionCommand)
        pluginManager.unregisterEvents(permissionListener)
    }

    override fun createTables() {
        transaction {
            SchemaUtils.create(
                GroupSchema,
                PlayerSchema,
                GroupPlayerSchema,
                GroupPermissionSchema,
                PlayerPermissionSchema
            )

            // Create default group if it does not exist
            Group.findById(Group.DefaultGroupUuid) ?: Group.new(Group.DefaultGroupUuid) {
                name = PERMISSION_DEFAULT_GROUP
                players = SizedCollection(listOf())
            }
        }
    }
}