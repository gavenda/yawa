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

import org.bukkit.command.CommandSender
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.HelpList

/**
 * Plugin main command.
 */
class RootCommand : Command() {
    override val commands = listOf("yawa")
    override fun execute(sender: CommandSender, args: List<String>) {
        HelpList()
            .command("yawa reload", listOf("<config>"), "Reloads the plugin", Permission.RELOAD)
            .generate(sender)
            .forEach { sender.sendMessage(it) }
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return subCommandKeys.toList()
    }

}

/**
 * Reloads the plugin.
 */
class ReloadCommand : Command() {
    override val permission = Permission.RELOAD
    override fun execute(sender: CommandSender, args: List<String>) {
        when (args.size) {
            1 -> {
                if (args[0] == "config") {
                    plugin.reloadConfig()
                    plugin.loadConfig()

                    sender.sendMessageUsingKey(Message.PluginReloadConfig)
                }
            }
        }
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return when (args.size) {
            1 -> listOf("config")
            else -> emptyList()
        }
    }
}