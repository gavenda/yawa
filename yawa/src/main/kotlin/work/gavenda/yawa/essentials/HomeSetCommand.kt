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
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.*
import work.gavenda.yawa.api.Command

class HomeSetCommand : Command() {
    override val permission = Permission.ESSENTIALS_HOME_SET
    override val commands = listOf("sethome", "sh")

    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) return

        scheduler.runTaskAsynchronously(plugin) { _ ->
            transaction {
                val playerHome = PlayerHomeDb.findById(sender.uniqueId) ?: PlayerHomeDb.new(sender.uniqueId) {}

                playerHome.apply {
                    world = sender.location.world.uid
                    x = sender.location.x
                    y = sender.location.y
                    z = sender.location.z
                }
            }

            sender.sendMessageUsingKey(Message.EssentialsHomeSet)
        }
    }
}