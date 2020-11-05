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

package work.gavenda.yawa.sleep

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBedEnterEvent
import org.bukkit.event.player.PlayerBedLeaveEvent
import org.bukkit.event.player.PlayerQuitEvent
import work.gavenda.yawa.Config
import work.gavenda.yawa.Message
import work.gavenda.yawa.api.Placeholder
import work.gavenda.yawa.api.sendMessageIf
import work.gavenda.yawa.api.translateColorCodes
import work.gavenda.yawa.parseWithDefaultLocale
import work.gavenda.yawa.parseWithLocale
import java.util.*

/**
 * Sleep feature bed listener.
 */
class SleepListener(
    private val sleepingWorlds: MutableSet<UUID>
) : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onBedEnter(event: PlayerBedEnterEvent) {
        val world = event.bed.world
        val player = event.player

        if (event.bedEnterResult != PlayerBedEnterEvent.BedEnterResult.OK) return

        val message = Placeholder
            .withContext(player, world)
            .parseWithLocale(player, Message.PlayerEnterBed)
            .translateColorCodes()

        world.sendMessageIf(message) { Config.Sleep.Chat.Enabled }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player

        if(player.sleepKicked) {
            val kickMessageBroadcast = Placeholder
                .withContext(player)
                .parseWithDefaultLocale(Message.SleepKickMessageBroadcast)
                .translateColorCodes()

            event.quitMessage = kickMessageBroadcast
            player.sleepKicked = false
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onBedLeave(event: PlayerBedLeaveEvent) {
        val world = event.bed.world
        val player = event.player

        if (world.uid in sleepingWorlds) return

        val message = Placeholder
            .withContext(player, world)
            .parseWithLocale(player, Message.PlayerLeftBed)
            .translateColorCodes()

        world.sendMessageIf(message) { Config.Sleep.Chat.Enabled }
    }

}