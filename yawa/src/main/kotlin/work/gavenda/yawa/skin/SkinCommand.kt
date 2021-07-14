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

package work.gavenda.yawa.skin

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import work.gavenda.yawa.Permission
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.HelpList

private val skinCommands = listOf("skin", "yawa:skin")

class SkinCommand : Command(commands = skinCommands) {

    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) return

        HelpList()
            .command("skin player", listOf("<player>"), "Apply a skin from a premium player", Permission.SKIN_PLAYER)
            .command("skin url", listOf("<url>", "<slim:true>"), "Apply a skin from a url", Permission.SKIN_URL)
            .command("skin reset", listOf(), "Resets your skin", Permission.SKIN_URL)
            .generate(sender)
            .forEach { sender.sendMessage(it) }
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return subCommandKeys.toList()
    }

}