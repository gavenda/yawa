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

const val HELP_PAGE_SIZE = 9

/**
 * Utilities for creating a consistent help list.
 */
class HelpList {

    data class Help(val text: String, val args: List<String>)

    private val commandMap = mutableMapOf<String, Help>()

    /**
     * Add a command to this help list.
     */
    fun command(name: String, args: List<String>, helpText: String): HelpList {
        commandMap[name] = Help(helpText, args)
        return this
    }

    /**
     * Generates the help list.
     * @param page page number, defaults to 1.
     */
    fun generate(page: Int = 1): String {
        val sb = StringBuilder()
        val commands = commandMap.keys.chunked(HELP_PAGE_SIZE)

        if (page == 0) return ""
        if (page > commands.size) return ""

        val commandsPaged = commands[page - 1]

        for (command in commandsPaged) {
            val help = commandMap.getValue(command)

            // Gold color
            sb.append("&6")
            sb.append("/")
            sb.append(command)
            sb.append(" ")
            // Reset color
            sb.append("&r")

            for (arg in help.args) {
                sb.append(arg)
                sb.append(" ")
            }

            sb.append("&e» &r")
            sb.append(help.text)
            sb.append("\n")
        }

        return sb.toString()
            .dropLast(1)
            .translateColorCodes()
    }

    /**
     * Generates the help list enclosing the messages in a list.
     *
     * Does `generate().split("\n")`.
     *
     * @param page page number, defaults to 1
     */
    fun generateMessages(page: Int = 1): List<String> {
        return generate(page).split("\n")
    }

}