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

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

private val placeholderCommands = listOf("placeholders", "yawaapi:placeholders")

class PlaceholderCommand : Command("yawa.api.placeholder", placeholderCommands) {

    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) return

        Placeholder
            .withContext(sender, sender.world)
            .asHelpList()
            .forEach {
                sender.sendMessage(it.translateColorCodes())
            }
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return emptyList()
    }

}