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
import org.bukkit.event.player.PlayerTeleportEvent
import work.gavenda.yawa.Message
import work.gavenda.yawa.Permission
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.sendMessageUsingKey

class BackCommand : Command() {
    override val permission = Permission.ESSENTIALS_TELEPORT_DEATH
    override val commands = listOf("back", "b")
    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) return

        val lastDeathLocation = sender.lastDeathLocation

        if (lastDeathLocation != null) {
            sender.teleportAsync(lastDeathLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept {
                sender.sendMessageUsingKey(Message.EssentialsTeleportDeath)
            }
        } else {
            sender.sendMessageUsingKey(Message.EssentialsTeleportErrorNoDeath)
        }
    }
}