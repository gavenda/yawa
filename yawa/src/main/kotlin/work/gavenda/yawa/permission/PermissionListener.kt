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

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.plugin.Plugin
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.api.bukkitAsyncTask

class PermissionListener(val plugin: Plugin) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerLogin(e: PlayerLoginEvent) {
        if (e.result != PlayerLoginEvent.Result.ALLOWED) return

        val player = e.player

        bukkitAsyncTask(plugin) {
            transaction {
                PlayerDb.findById(player.uniqueId) ?: PlayerDb.new(player.uniqueId) {
                    val defaultGroup = Group.findById(Group.DefaultGroupUuid)
                        ?: throw IllegalStateException("Default permission group does not exist in the database")

                    name = player.name
                    groups = SizedCollection(listOf(defaultGroup))
                }
            }
        }
    }

}