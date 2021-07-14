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

package work.gavenda.yawa.skin

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.api.applySkin
import work.gavenda.yawa.plugin
import work.gavenda.yawa.scheduler

/**
 * Applies skin on player login.
 */
class SkinListener : Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerLogin(e: PlayerLoginEvent) {
        if (e.result != PlayerLoginEvent.Result.ALLOWED) return

        val player = e.player

        scheduler.runTaskAsynchronously(plugin) { _ ->
            transaction {
                val playerTexture = PlayerTexture.findById(player.uniqueId)
                if (playerTexture != null) {
                    // Existing texture on database, apply
                    player.applySkin(playerTexture.texture, playerTexture.signature)
                    return@transaction
                }

                player.restoreSkin()
            }
        }
    }
}