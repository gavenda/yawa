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

package work.gavenda.yawa

import org.bukkit.command.CommandSender
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.translateColorCodes

/**
 * Plugin main command.
 */
class YawaCommand : Command("yawa") {

    override fun execute(sender: CommandSender, args: Array<String>) {
    }

    override fun onTab(sender: CommandSender, args: Array<String>): List<String>? {
        return null
    }

}

/**
 * Reloads the plugin.
 */
class YawaReloadCommand : Command("yawa.reload") {
    override fun execute(sender: CommandSender, args: Array<String>) {
        Plugin.Instance.onDisable()
        Plugin.Instance.onEnable()

        sender.sendMessage(Config.Messages.PluginReload.translateColorCodes())
    }

    override fun onTab(sender: CommandSender, args: Array<String>): List<String>? {
        return null
    }
}

/**
 * Only reloads the plugin configuration.
 */
class YawaReloadConfigCommand : Command("yawa.reload.config") {
    override fun execute(sender: CommandSender, args: Array<String>) {
        Plugin.Instance.reloadConfig()
        Plugin.Instance.loadConfig()

        sender.sendMessage(Config.Messages.PluginReloadConfig.translateColorCodes())
    }

    override fun onTab(sender: CommandSender, args: Array<String>): List<String>? {
        return null
    }
}