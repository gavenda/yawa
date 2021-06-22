/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (C) 2021 Gavenda <gavenda@disroot.org>
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

package work.gavenda.yawa.login

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import work.gavenda.yawa.api.asAudience
import work.gavenda.yawa.server

class PremiumListener : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        if (player.isVerified) {
            event.joinMessage = null

            val hover = HoverEvent.showText(
                Component.text("Verified Mojang Account", NamedTextColor.GREEN)
            )

            val message = Component.text(player.name, NamedTextColor.GOLD)
                .hoverEvent(hover)
                .append(Component.text(" joined the game", NamedTextColor.YELLOW))

            val onlinePlayers = server.onlinePlayers

            for (onlinePlayer in onlinePlayers) {
                onlinePlayer.asAudience().sendMessage(message)
            }
        }
    }

}