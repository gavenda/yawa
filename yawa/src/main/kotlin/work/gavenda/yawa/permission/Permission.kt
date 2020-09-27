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

import org.bukkit.event.HandlerList
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.Config
import work.gavenda.yawa.DisabledCommand
import work.gavenda.yawa.Yawa

private lateinit var permissionListener: PermissionListener
private val permissionCommand = PermissionCommand().apply {
    sub(PermissionPlayerCommand(), "player")
    sub(PermissionGroupCommand(), "group")
}

/**
 * Enable permission feature.
 */
fun Yawa.enablePermission() {
    if (Config.Permission.Disabled) return

    slF4JLogger.warn("Permissions feature is enabled, please use LuckPerms if you're going for scale")

    // Init tables if not created
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

    // Instantiate event listeners
    permissionListener = PermissionListener(this)

    // In-case of reload lol
    server.onlinePlayers.forEach {
        it.permissionAttachment = it.addAttachment(this)
        it.calculatePermissions()
    }

    // Register commands
    getCommand("permission")?.setExecutor(permissionCommand)

    // Register event listeners
    server.pluginManager.registerEvents(permissionListener, this)
    server.pluginManager.registerEvents(permissionCommand, this)
}

/**
 * Disable permission feature.
 */
fun Yawa.disablePermission(reload: Boolean = false) {
    if (Config.Permission.Disabled) return

    // Unregister event listeners
    HandlerList.unregisterAll(permissionCommand)
    HandlerList.unregisterAll(permissionListener)

    // Disable command
    if (reload) {
        getCommand("permission")?.setExecutor(DisabledCommand)
    } else {
        getCommand("permission")?.setExecutor(null)
    }

    // Remove attachment
    server.onlinePlayers.forEach {
        it.removeAttachment()
    }
}