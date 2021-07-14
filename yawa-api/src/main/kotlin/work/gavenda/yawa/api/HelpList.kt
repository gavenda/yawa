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

package work.gavenda.yawa.api

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender

const val HELP_PAGE_SIZE = 9

/**
 * Utilities for creating a consistent help list.
 */
class HelpList {

    data class Help(val text: String, val args: List<String>, val permission: String)

    private val commandMap = mutableMapOf<String, Help>()

    /**
     * Add a command to this help list.
     */
    fun command(name: String, args: List<String>, helpText: String, permission: String = ""): HelpList {
        commandMap[name] = Help(helpText, args, permission)
        return this
    }

    /**
     * Generates the help list.
     * @param sender the command sender
     * @param page page number, defaults to 1.
     */
    fun generate(sender: CommandSender, page: Int = 1): List<Component> {
        val generatedList = mutableListOf<Component>()
        val commands = commandMap.keys.chunked(HELP_PAGE_SIZE)

        if (page == 0) return emptyList()
        if (page > commands.size) return emptyList()

        val commandsPaged = commands[page - 1]

        // No commands empty string
        if (commandsPaged.isEmpty()) return emptyList()

        for (command in commandsPaged) {
            val help = commandMap.getValue(command)

            // Sender must have permission
            if (sender.hasPermission(help.permission).not()) continue

            var component = Component.text("/", NamedTextColor.GOLD)
                .append(Component.text(command, NamedTextColor.WHITE))
                .append(Component.text(" "))

            for (arg in help.args) {
                component = component.append(Component.text(arg))
                    .append(Component.text(" "))
            }

            component = component.append(Component.text("» ", NamedTextColor.YELLOW))
                .append(Component.text(help.text, NamedTextColor.WHITE))

            generatedList.add(component)
        }

        // We have reach here, return no permissions message
        if (generatedList.isEmpty()) return listOf(COMMAND_NO_PERMISSION)

        return generatedList
    }

}