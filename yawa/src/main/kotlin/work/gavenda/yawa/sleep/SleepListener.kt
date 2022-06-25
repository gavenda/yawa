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

package work.gavenda.yawa.sleep

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBedEnterEvent
import org.bukkit.event.player.PlayerBedLeaveEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import work.gavenda.yawa.Config
import work.gavenda.yawa.Message
import work.gavenda.yawa.api.compat.quitMessageCompat
import work.gavenda.yawa.api.compat.sendMessageCompat
import work.gavenda.yawa.api.placeholder.Placeholders
import work.gavenda.yawa.parseUsingDefaultLocale
import work.gavenda.yawa.parseWithLocale
import java.util.*

/**
 * Sleep feature bed listener.
 */
class SleepListener(
    private val sleepingWorlds: MutableSet<UUID>
) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBedEnter(event: PlayerBedEnterEvent) {
        val world = event.bed.world
        val player = event.player

        if (world.uid in sleepingWorlds) return
        if (event.bedEnterResult != PlayerBedEnterEvent.BedEnterResult.OK) return

        val message = Placeholders
            .withContext(player, world)
            .parseWithLocale(player, Message.PlayerEnterBed)

        if (Config.Sleep.Chat.Enabled) {
            world.sendMessageCompat(message)
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.sleepKicked = false
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player

        if (player.sleepKicked) {
            val kickMessageBroadcast = Placeholders
                .withContext(player)
                .parseUsingDefaultLocale(Message.SleepKickMessageBroadcast)

            event.quitMessageCompat = kickMessageBroadcast
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBedLeave(event: PlayerBedLeaveEvent) {
        val world = event.bed.world
        val player = event.player

        if (world.uid in sleepingWorlds) return

        val message = Placeholders
            .withContext(player, world)
            .parseWithLocale(player, Message.PlayerLeftBed)

        if (Config.Sleep.Chat.Enabled) {
            world.sendMessageCompat(message)
        }
    }

}