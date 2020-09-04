package work.gavenda.yawa.ping

import org.bukkit.ChatColor
import org.bukkit.scoreboard.DisplaySlot
import work.gavenda.yawa.Config
import work.gavenda.yawa.Plugin
import work.gavenda.yawa.api.bukkitTimerTask
import work.gavenda.yawa.api.isAfk
import work.gavenda.yawa.api.latencyInMillis

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
            val displayFormat = String.format("%-16s&e%3s ", name, afk).plus("&6»")
            val playerListName = ChatColor.translateAlternateColorCodes('&', displayFormat)

            val serverNameFormat = "&6${Config.Ping.ServerName}"
            val displayServerNameFormat = ChatColor.translateAlternateColorCodes('&', serverNameFormat)

            // Display
            player.playerListHeader = displayServerNameFormat
            player.setPlayerListName(playerListName)

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