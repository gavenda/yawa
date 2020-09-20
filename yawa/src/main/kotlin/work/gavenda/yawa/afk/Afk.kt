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
import work.gavenda.yawa.Permission
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
        getCommand("afk")?.setExecutor(DisabledCommand)
        return
    }

    // Command handler
    getCommand("afk")?.setExecutor(AfkCommand())

    // Tasks
    afkTaskId = bukkitTimerTask(this, 0, 20) {
        server.onlinePlayers
            .filter { it.hasPermission(Permission.AFK) }
            .forEach { player ->
                val afkDelta = System.currentTimeMillis() - player.lastInteractionMillis
                val afkSeconds = TimeUnit.MILLISECONDS.toSeconds(afkDelta)
                val isNotAfk = !player.isAfk

                if (isNotAfk && afkSeconds > Config.Afk.Seconds) {
                    player.isAfk = true

                    val message = Placeholder
                        .withContext(player)
                        .parse(Config.Messages.AfkEntryMessage)
                        .translateColorCodes()
                    val selfMessage = Placeholder
                        .withContext(player)
                        .parse(Config.Messages.PlayerAfkStart)
                        .translateColorCodes()

                    player.world.sendMessageIf(message) {
                        Config.Afk.MessageEnabled
                    }
                    player.sendMessage(selfMessage)
                }

                if (player.isAfk) {
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

    // Register event listeners
    server.pluginManager.registerEvents(afkListener, this)
}

/**
 * Disable afk feature.
 * @param reload set to true if reloading, defaults to false
 */
fun Plugin.disableAfk(reload: Boolean = false) {
    if (Config.Afk.Disabled) return

    // Events
    HandlerList.unregisterAll(afkListener)
    // Tasks
    server.scheduler.cancelTask(afkTaskId)
    // Command handlers
    if (reload) {
        getCommand("afk")?.setExecutor(DisabledCommand)
    } else {
        getCommand("afk")?.setExecutor(null)
    }
}