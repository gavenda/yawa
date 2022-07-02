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

package work.gavenda.yawa.api

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import work.gavenda.yawa.api.compat.PluginEnvironment
import work.gavenda.yawa.api.compat.PLUGIN_ENVIRONMENT
import work.gavenda.yawa.api.placeholder.PlaceholderCommand
import work.gavenda.yawa.api.placeholder.Placeholders
import work.gavenda.yawa.api.placeholder.provider.PlayerPlaceholderProvider
import work.gavenda.yawa.api.placeholder.provider.ServerPlaceholderProvider
import work.gavenda.yawa.api.placeholder.provider.WorldPlaceholderProvider

/**
 * Yawa API plugin entry point.
 */
class YawaAPI : JavaPlugin() {

    companion object {
        lateinit var Instance: YawaAPI
    }

    lateinit var adventure: BukkitAudiences
    private val placeholderCommand = PlaceholderCommand()

    override fun onEnable() {
        Instance = this
        adventure = BukkitAudiences.create(this)

        // Register placeholders
        Placeholders.register(PlayerPlaceholderProvider())
        Placeholders.register(WorldPlaceholderProvider())
        Placeholders.register(ServerPlaceholderProvider())

        if (PLUGIN_ENVIRONMENT == PluginEnvironment.PAPER) {
            server.pluginManager.registerEvents(placeholderCommand, this)
        }
        getCommand("placeholders")?.setExecutor(placeholderCommand)
    }

    override fun onDisable() {
        Placeholders.clear()
        adventure.close()

        getCommand("placeholders")?.setExecutor(null)
        if (PLUGIN_ENVIRONMENT == PluginEnvironment.PAPER) {
            HandlerList.unregisterAll(placeholderCommand)
        }
    }
}
