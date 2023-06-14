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
package work.gavenda.yawa.discord

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import work.gavenda.yawa.api.toPlainText
import work.gavenda.yawa.discordAlert
import work.gavenda.yawa.sleep.sleepKicked

class PlayerListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerJoin(e: PlayerJoinEvent) {
        e.player.discordAlert("${e.player.name} joined the server", color = NamedTextColor.GREEN)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerLeave(e: PlayerQuitEvent) {
        if (e.player.sleepKicked) return
        e.player.discordAlert("${e.player.name} left the server", color = NamedTextColor.RED)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerAdvancement(e: PlayerAdvancementDoneEvent) {
        val advancementDisplay = e.advancement.display ?: return
        if (!advancementDisplay.doesAnnounceToChat()) return
        val advancementTitle = advancementDisplay.title().toPlainText()
        e.player.discordAlert("${e.player.name} has made the advancement '${advancementTitle}'")
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerDeath(e: PlayerDeathEvent) {
        e.deathMessage()?.let {
            e.entity.discordAlert(it, color = NamedTextColor.BLACK)
        }
    }
}