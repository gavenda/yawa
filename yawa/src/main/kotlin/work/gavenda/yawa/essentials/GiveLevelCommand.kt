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

package work.gavenda.yawa.essentials

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import work.gavenda.yawa.*
import work.gavenda.yawa.api.Command

class GiveLevelCommand : Command() {
    override val permission = Permission.ESSENTIALS_GIVE_LEVEL
    override val commands = listOf(Commands.GIVE_LEVEL)

    override fun execute(sender: CommandSender, args: List<String>) {
        try {
            if (sender !is Player) return
            val target = server.getPlayer(args[0]) ?: return
            val levels = args[1].toInt()

            if (levels > sender.level) {
                sender.sendMessageUsingKey(Message.EssentialsGiveLevelErrorNotEnoughLevel)
                return
            }

            sender.level = sender.level - levels;
            target.level = target.level + levels;

            sender.sendMessageUsingKey(
                Message.EssentialsGiveLevel, mapOf(
                    "level" to levels,
                    "target" to target.displayName()
                )
            )
        } catch (ex: NumberFormatException) {
            sender.sendMessageUsingKey(Message.EssentialsGiveLevelErrorInvalidLevel)
            logger.warn("Cannot give level, level is not a number. Input: ${args[1]}")
        }
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        if (sender !is Player) return emptyList()
        return when (args.size) {
            0 -> server.onlinePlayers.map { it.name }
            1 -> server.onlinePlayers
                .filter { it.name.contains(args[0]) }
                .map { it.name }

            2 -> listOf("<levels>")
            else -> emptyList()
        }
    }
}