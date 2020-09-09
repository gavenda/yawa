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

package work.gavenda.yawa.afk

import org.bukkit.event.HandlerList
import work.gavenda.yawa.Config
import work.gavenda.yawa.DisabledCommand
import work.gavenda.yawa.Plugin
import work.gavenda.yawa.api.*
import java.util.concurrent.TimeUnit

private var afkTaskId = -1
private val afkListener = AfkListener()

/**
 * Enable afk feature.
 */
fun Plugin.enableAfk() {
    if (Config.Afk.Disabled) {
        getCommand("afk")?.setExecutor(DisabledCommand())
        return
    }

    // Command handler
    getCommand("afk")?.setExecutor(AfkCommand())

    // Tasks
    afkTaskId = bukkitTimerTask(this, 0, 20) {
        server.onlinePlayers.forEach { player ->
            val afkDelta = System.currentTimeMillis() - player.lastInteractionMillis
            val afkSeconds = TimeUnit.MILLISECONDS.toSeconds(afkDelta)
            val isNotAfk = !player.afk

            if (isNotAfk && afkSeconds > 30) {
                player.afk = true

                val message = Placeholder
                    .withContext(player)
                    .parse(Config.Messages.AfkEntryMessage)
                    .translateColorCodes()

                player.world.sendMessageIf(message) {
                    Config.Afk.MessageEnabled
                }
            }

            if(player.afk) {
                player.setPlayerListName(
                    Placeholder.withContext(player)
                        .parse(Config.Afk.PlayerListName)
                        .translateColorCodes()
                )
            } else {
                player.setPlayerListName(null)
            }
        }
    }

    // Register events
    server.pluginManager.registerEvents(afkListener, this)
}

/**
 * Disable afk feature.
 */
fun Plugin.disableAfk() {
    if (Config.Afk.Disabled) return

    // Events
    HandlerList.unregisterAll(afkListener)
    // Tasks
    server.scheduler.cancelTask(afkTaskId)
    // Command handlers
    getCommand("afk")?.setExecutor(null)
}