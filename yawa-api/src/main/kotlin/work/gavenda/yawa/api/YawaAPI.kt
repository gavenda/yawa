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

package work.gavenda.yawa.api

import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import work.gavenda.yawa.api.providers.PlayerPlaceholderProvider
import work.gavenda.yawa.api.providers.ServerPlaceholderProvider
import work.gavenda.yawa.api.providers.WorldPlaceholderProvider

/**
 * Yawa API plugin entry point.
 */
class YawaAPI : JavaPlugin() {

    companion object {
        lateinit var Instance: YawaAPI
        val MiniMessage = net.kyori.adventure.text.minimessage.MiniMessage.get()
    }

    private val placeholderCommand = PlaceholderCommand()

    override fun onEnable() {
        Instance = this

        // Register placeholders
        Placeholder.register(PlayerPlaceholderProvider())
        Placeholder.register(WorldPlaceholderProvider())
        Placeholder.register(ServerPlaceholderProvider())

        server.pluginManager.registerEvents(placeholderCommand, this)
        getCommand("placeholders")?.setExecutor(placeholderCommand)
    }

    override fun onDisable() {
        Placeholder.clear()

        getCommand("placeholders")?.setExecutor(null)
        HandlerList.unregisterAll(placeholderCommand)
    }
}
