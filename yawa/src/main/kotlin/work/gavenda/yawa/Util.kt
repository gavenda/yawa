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

package work.gavenda.yawa

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.PluginManager
import org.bukkit.scheduler.BukkitScheduler
import work.gavenda.yawa.api.compat.sendMessageCompat
import work.gavenda.yawa.api.placeholder.PlaceholderContext
import work.gavenda.yawa.api.placeholder.Placeholders
import work.gavenda.yawa.api.toPlainText
import work.gavenda.yawa.discord.DiscordFeature
import work.gavenda.yawa.discord.avatarUrl
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
fun PlaceholderContext.parseWithLocale(player: Player, key: String, params: Map<String, Any?> = mapOf()): Component {
    return parse(Messages.forPlayer(player).get(key), params)
}

/**
 * Extend placeholder context to parse for the server's default locale.
 */
fun PlaceholderContext.parseUsingDefaultLocale(key: String, params: Map<String, Any?> = mapOf()): Component {
    return parse(Messages.useDefault().get(key), params)
}

/**
 * Utility function for sending a message with locale and placeholder support depending on context.
 */
fun CommandSender.sendMessageUsingKey(key: String, params: Map<String, Any?> = mapOf()) {
    if (this is Player) {
        sendMessageCompat(
            Placeholders
                .withContext(this)
                .parseWithLocale(this, key, params)
        )
    } else {
        val message = Placeholders.deserialize(
            Messages.useDefault()
                .get(key)
        )
        sendMessageCompat(message)
    }
}

fun Player.discordAlert(text: String, color: TextColor = NamedTextColor.YELLOW) {
    logger.info(text)
    DiscordFeature.sendAlert(text, avatarUrl, color)
}

fun Player.discordAlert(component: Component, color: TextColor = NamedTextColor.YELLOW) {
    discordAlert(component.toPlainText(), color)
}
