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
import work.gavenda.yawa.*
import work.gavenda.yawa.api.Placeholder
import java.util.concurrent.TimeUnit

const val SB_NAME = "ping"
const val SB_CRITERIA = "dummy"
const val SB_DISPLAY_NAME = "ms"

object PingFeature : PluginFeature {
    override val isDisabled get() = Config.Ping.Disabled

    private var pingTaskId = -1

    private val pingCommand = PingCommand()
    private val playerPingPlaceholder = PlayerPingPlaceholder()

    private val scoreboard = server.scoreboardManager.newScoreboard
    private val objective = scoreboard.registerNewObjective(SB_NAME, SB_CRITERIA, SB_DISPLAY_NAME).apply {
        displaySlot = DisplaySlot.PLAYER_LIST
    }

    override fun enableCommands() {
        plugin.getCommand("ping")?.setExecutor(pingCommand)
    }

    override fun disableCommands() {
        plugin.getCommand("ping")?.setExecutor(DisabledCommand)
    }

    override fun registerPlaceholders() {
        Placeholder.register(playerPingPlaceholder)
    }

    override fun unregisterPlaceholders() {
        Placeholder.unregister(playerPingPlaceholder)
    }

    override fun registerTasks() {
        val pingTask = PingTask(scoreboard, objective)
        val secondsInTicks = TimeUnit.SECONDS.toTicks(5)

        pingTaskId = scheduler.scheduleSyncRepeatingTask(plugin, pingTask, 0, secondsInTicks)
    }

    override fun unregisterTasks() {
        scheduler.cancelTask(pingTaskId)
    }
}