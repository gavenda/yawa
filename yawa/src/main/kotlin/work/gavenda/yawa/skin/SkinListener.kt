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

package work.gavenda.yawa.skin

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.api.Plugin
import work.gavenda.yawa.api.applySkin
import work.gavenda.yawa.api.bukkitAsyncTask
import work.gavenda.yawa.skin.restoreSkin

/**
 * Applies skin on player join.
 */
class SkinListener : Listener {

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        val onlineMode = e.player.server.onlineMode

        bukkitAsyncTask(Plugin.Instance) {
            transaction {
                val playerTexture = PlayerTexture.findById(player.uniqueId)

                if (playerTexture != null) {
                    // Existing texture on database, apply
                    player.applySkin(playerTexture.texture, playerTexture.signature)
                } else if (!onlineMode) {
                    // Server in offline mode
                    player.restoreSkin()
                }
            }
        }
    }

}