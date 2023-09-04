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

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.*
import work.gavenda.yawa.api.Command

class WarpCommand : Command() {
    override val permission = Permission.ESSENTIALS_WARP_TELEPORT
    override val commands = listOf("warp", "location", "loc", "l")

    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) return
        if (args.isEmpty()) return

        val locationName = args[0]

        asyncScheduler.runNow(plugin) {
            transaction {
                val playerLocationDb = PlayerLocationDb
                    .find { (PlayerLocationSchema.playerUuid eq sender.uniqueId) and (PlayerLocationSchema.name eq locationName) }
                    .firstOrNull()

                if (playerLocationDb != null) {
                    val world = server.getWorld(playerLocationDb.world)

                    if (world == null) {
                        sender.sendMessageUsingKey(Message.EssentialsTeleportErrorNoHomeWorld)
                        playerLocationDb.delete()
                        return@transaction
                    }

                    val location = Location(world, playerLocationDb.x, playerLocationDb.y, playerLocationDb.z)

                    sender.teleportAsync(location, PlayerTeleportEvent.TeleportCause.COMMAND).thenRun {
                        sender.sendMessageUsingKey(
                            Message.EssentialsWarpTeleport, mapOf(
                                "location-name" to Component.text(playerLocationDb.name, NamedTextColor.WHITE)
                            )
                        )
                    }
                } else {
                    sender.sendMessageUsingKey(
                        Message.EssentialsTeleportErrorNoLocation, mapOf(
                            "location-name" to Component.text(locationName, NamedTextColor.WHITE)
                        )
                    )
                }
            }
        }
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        if (sender !is Player) return emptyList()
        return when (args.size) {
            0 -> transaction {
                PlayerLocationDb
                    .find { PlayerLocationSchema.playerUuid eq sender.uniqueId }
                    .map { it.name }
            }

            1 -> transaction {
                PlayerLocationDb
                    .find { (PlayerLocationSchema.playerUuid eq sender.uniqueId) and (PlayerLocationSchema.name like args[0] + "%") }
                    .map { it.name }
            }

            else -> emptyList()
        }
    }
}