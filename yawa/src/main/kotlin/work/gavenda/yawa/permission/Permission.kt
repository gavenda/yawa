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
import work.gavenda.yawa.Plugin
import java.util.*


private lateinit var permissionListener: PermissionListener

/**
 * Enable permission feature.
 */
fun Plugin.enablePermission() {
    if (Config.Permission.Disabled) return

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
    // Register event listeners
    server.pluginManager.registerEvents(permissionListener, this)
}

/**
 * Disable permission feature.
 */
fun Plugin.disablePermission() {
    if (Config.Permission.Disabled) return

    // Unregister event listeners
    HandlerList.unregisterAll(permissionListener)
}