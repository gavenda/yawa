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

package work.gavenda.yawa.afk

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import work.gavenda.yawa.Config
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.Placeholder
import work.gavenda.yawa.api.isAfk
import work.gavenda.yawa.api.sendMessageIf

class AfkCommand : Command("yawa.afk") {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) return

        sender.isAfk = true

        val message = Placeholder
            .withContext(sender)
            .parse(Config.Messages.AfkEntryMessage)

        sender.world.sendMessageIf(message) {
            Config.Afk.MessageEnabled
        }
    }

    override fun onTab(sender: CommandSender, args: Array<String>): List<String>? {
        return null
    }

}