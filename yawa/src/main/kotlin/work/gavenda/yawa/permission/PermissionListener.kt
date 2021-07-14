/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 *  Copyright (C) 2021 Gavenda <gavenda@disroot.org>
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
 *
 */

package work.gavenda.yawa.permission

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.plugin
import work.gavenda.yawa.scheduler

/**
 * Listens to player join and quit events.
 */
class PermissionListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player

        // Add attachment
        PermissionFeature.attachTo(player)

        scheduler.runTaskAsynchronously(plugin) { _ ->
            transaction {
                PlayerDb.findById(player.uniqueId) ?: PlayerDb.new(player.uniqueId) {
                    val defaultGroup = Group.findById(Group.DefaultGroupUuid)
                        ?: throw IllegalStateException("Default permission group does not exist in the database")

                    name = player.name
                    groups = SizedCollection(listOf(defaultGroup))
                }
            }

            // Calculate
            player.calculatePermissions()
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val player = e.player

        // Remove attachment
        player.removeAttachment()
    }

}