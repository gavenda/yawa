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

package work.gavenda.yawa.skin

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.HelpList

class SkinCommand : Command("yawa.skin") {

    private val helpList = HelpList()
        .command("skin", listOf(), "Shows this command list")
        .command("skin player", listOf("<player>"), "Apply a skin from a premium player")
        .command("skin url", listOf("<url>", "<slim:true>"), "Apply a skin from a url")
        .command("skin reset", listOf(), "Resets your skin")
        .generateMessages()

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) return
        helpList.forEach(sender::sendMessage)
    }

    override fun onTab(sender: CommandSender, args: Array<String>): List<String>? {
        return subCommandKeys.toList()
    }

}