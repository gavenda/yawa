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

package work.gavenda.yawa.chat

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import work.gavenda.yawa.Config
import work.gavenda.yawa.Message
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.Placeholder
import work.gavenda.yawa.api.translateColorCodes
import work.gavenda.yawa.sendMessageUsingKey
import work.gavenda.yawa.server

class WhisperCommand : Command(
    commands = listOf("whisper", "yawa:whisper", "w", "msg", "tell")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) return
        val targetPlayerArg = args[0]
        val message = args.drop(1).joinToString(separator = " ")
        val target = server.getPlayer(targetPlayerArg)

        if (target == null) {
            sender.sendMessageUsingKey(Message.WhisperPlayerNotFound)
            return
        }
        if (message.isBlank()) return

        val messageTo = Placeholder.withContext(target)
            .parse(Config.Chat.FormatMessageTo)
            .plus(message)
            .translateColorCodes()
        val messageFrom = Placeholder.withContext(sender)
            .parse(Config.Chat.FormatMessageFrom)
            .plus(message)
            .translateColorCodes()

        sender.sendMessage(messageTo)
        target.sendMessage(messageFrom)
        target.lastWhisperPlayer = sender.name
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return when (args.size) {
            1 -> server.onlinePlayers
                .map { it.name }
                .toList()
            else -> listOf("<message>")
        }
    }
}