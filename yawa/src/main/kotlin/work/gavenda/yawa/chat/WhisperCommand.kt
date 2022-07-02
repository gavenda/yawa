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

package work.gavenda.yawa.chat

import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import work.gavenda.yawa.Config
import work.gavenda.yawa.Message
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.compat.sendMessageCompat
import work.gavenda.yawa.api.placeholder.Placeholders
import work.gavenda.yawa.sendMessageUsingKey
import work.gavenda.yawa.server

class WhisperCommand : Command() {
    override val commands = listOf("whisper", "w", "msg", "tell")

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

        val messageTo = Placeholders.withContext(target)
            .parse(Config.Chat.FormatMessageTo)
            .append(Component.text(message))
        val messageFrom = Placeholders.withContext(sender)
            .parse(Config.Chat.FormatMessageFrom)
            .append(Component.text(message))

        sender.sendMessageCompat(messageTo)
        target.sendMessageCompat(messageFrom)
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