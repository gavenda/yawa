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

package work.gavenda.yawa.afk

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import work.gavenda.yawa.*
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.afk
import work.gavenda.yawa.api.placeholder.Placeholders

class AfkCommand : Command() {
    override val permission = Permission.AFK
    override val commands = listOf("afk")

    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) return
        if (sender.hasPermission(Permission.AFK).not()) return

        sender.afk = true

        val message = Placeholders
            .withContext(sender)
            .parseWithLocale(sender, Message.AfkEntryMessage)

        if (Config.Afk.MessageEnabled) {
            sender.world.sendMessage(message)
        }
        sender.sendMessageUsingKey(Message.PlayerAfkStart)
    }

}