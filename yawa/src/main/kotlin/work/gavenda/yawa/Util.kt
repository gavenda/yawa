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

package work.gavenda.yawa

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.PluginManager
import org.bukkit.scheduler.BukkitScheduler
import work.gavenda.yawa.api.YawaAPI
import work.gavenda.yawa.api.compat.sendMessageCompat
import work.gavenda.yawa.api.placeholder.Placeholder
import work.gavenda.yawa.api.placeholder.PlaceholderContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels
import java.util.concurrent.TimeUnit


/**
 * Easy access to the plugin instance.
 */
val plugin get() = Yawa.Instance

/**
 * Easy access to bukkit's server instance.
 */
val server: Server get() = Bukkit.getServer()

/**
 * Easy access to bukkit's scheduler.
 */
val scheduler: BukkitScheduler get() = Bukkit.getScheduler()

/**
 * Easy access to bukkit's plugin manager.
 */
val pluginManager: PluginManager get() = Bukkit.getPluginManager()

/**
 * Easy access to ProtocolLib protocol manager.
 */
val protocolManager: ProtocolManager get() = ProtocolLibrary.getProtocolManager()

/**
 * Convert time unit into minecraft ticks.
 */
fun TimeUnit.toTicks(d: Long): Long {
    return when (this) {
        TimeUnit.SECONDS -> d * 20L
        TimeUnit.MILLISECONDS -> TimeUnit.MILLISECONDS.toSeconds(d) * 20L
        TimeUnit.MINUTES -> TimeUnit.MINUTES.toSeconds(d) * 20L
        TimeUnit.HOURS -> TimeUnit.HOURS.toSeconds(d) * 20L
        TimeUnit.DAYS -> TimeUnit.DAYS.toSeconds(d) * 20L
        TimeUnit.MICROSECONDS -> TimeUnit.MICROSECONDS.toSeconds(d) * 20L
        TimeUnit.NANOSECONDS -> TimeUnit.NANOSECONDS.toSeconds(d) * 20L
        else -> throw AbstractMethodError()
    }
}

/**
 * Convenience method for register events, but only for this plugin.
 */
fun PluginManager.registerEvents(listener: Listener) {
    registerEvents(listener, plugin)
}

/**
 * This should be within plugin manager, but they didn't. Hence a convenience method so we don't always lookup the docs.
 * For the kotlin compiler, this should be unnecessary so we suppress the warning.
 */
@Suppress("unused")
fun PluginManager.unregisterEvents(listener: Listener) {
    HandlerList.unregisterAll(listener)
}

/**
 * Extend placeholder context to parse for a player locale.
 */
fun PlaceholderContext.parseWithLocale(player: Player, key: String): Component {
    return parse(
        Messages
            .forPlayer(player)
            .get(key)
    )
}

/**
 * Extend placeholder context to parse for the server's default locale.
 */
fun PlaceholderContext.parseWithDefaultLocale(key: String): Component {
    return parse(
        Messages
            .useDefault()
            .get(key)
    )
}

/**
 * Downloads the URL to the following file.
 * @param file the file location to download into
 * @return total bytes transferred
 */
fun URL.downloadTo(file: File): Long {
    val readableByteChannel = Channels.newChannel(openStream())
    val fileOutputStream = FileOutputStream(file)
    val fileChannel = fileOutputStream.channel

    var bytesTransferred = 0L
    var availableBytes: Long

    do {
        availableBytes = fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE)
        bytesTransferred += availableBytes
    } while (availableBytes > 0)

    return bytesTransferred
}

/**
 * Utility function for sending a message with locale and placeholder support depending on context.
 */
fun CommandSender.sendMessageUsingKey(key: String) {
    if (this is Player) {
        sendMessageCompat(
            Placeholder
                .withContext(this)
                .parseWithLocale(this, key)
        )
    } else {
        val miniMessage = YawaAPI.MiniMessage
        val message = miniMessage.parse(
            Messages.useDefault()
                .get(key)
        )
        sendMessageCompat(message)
    }
}