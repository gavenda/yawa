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

package work.gavenda.yawa.ping

import org.bukkit.scoreboard.DisplaySlot
import work.gavenda.yawa.Config
import work.gavenda.yawa.Plugin
import work.gavenda.yawa.api.*

private var pingTaskId = -1

const val SB_NAME = "ping"
const val SB_CRITERIA = "dummy"
const val SB_DISPLAY_NAME = "ms"

/**
 * Enable ping feature.
 */
fun Plugin.enablePing() {
    if (Config.Ping.Disabled) return

    val board = server.scoreboardManager.newScoreboard
    val objective = board.registerNewObjective(SB_NAME, SB_CRITERIA, SB_DISPLAY_NAME).apply {
        displaySlot = DisplaySlot.PLAYER_LIST
    }

    pingTaskId = bukkitTimerTask(this, 0, 20) {
        val onlinePlayers = server.onlinePlayers

        for (player in onlinePlayers) {
            val name = player.name
            val ping = player.latencyInMillis
            val afk = if (player.isAfk) "AFK" else ""
            val displayFormat = String.format("%s &e%s ", name, afk)
                .translateColorCodes()

            // Display
            player.playerListHeader = Placeholder
                .withContext(player)
                .parse(Config.Ping.ServerName)
                .translateColorCodes()
            player.setPlayerListName(displayFormat)

            // Update latency
            objective.getScore(player.name).apply {
                score = ping
            }

            player.scoreboard = board
        }
    }
}

/**
 * Disable ping feature.
 */
fun Plugin.disablePing() {
    if (Config.Ping.Disabled) return

    server.scheduler.cancelTask(pingTaskId)
}