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

import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.*
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.compat.schedulerCompat

class HomeCommand : Command() {
    override val permission = Permission.ESSENTIALS_HOME_TELEPORT
    override val commands = listOf("home", "h")

    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) return

        sender.schedulerCompat.runAtNextTickAsynchronously(plugin) {
            transaction {
                val playerHomeDb = PlayerHomeDb.findById(sender.uniqueId)

                if (playerHomeDb != null) {
                    val world = server.getWorld(playerHomeDb.world)

                    if (world == null) {
                        sender.sendMessageUsingKey(Message.EssentialsTeleportErrorNoHomeWorld)
                        playerHomeDb.delete()
                        return@transaction
                    }

                    val location = Location(world, playerHomeDb.x, playerHomeDb.y, playerHomeDb.z)

                    sender.teleportAsync(location, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept {
                        sender.sendMessageUsingKey(Message.EssentialsHomeTeleport)
                    }
                } else {
                    sender.sendMessageUsingKey(Message.EssentialsTeleportErrorNoHome)
                }
            }
        }
    }
}