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
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.*
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.compat.schedulerCompat

class WarpSetCommand : Command() {
    override val permission = Permission.ESSENTIALS_WARP_SET
    override val commands = listOf("setwarp", "setlocation", "setloc", "sl")

    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) return
        if (args.isEmpty()) return

        val locationName = args[0]

        sender.schedulerCompat.runAtNextTickAsynchronously(plugin) {
            transaction {
                val playerLocation = PlayerLocationDb
                    .find { (PlayerLocationSchema.playerUuid eq sender.uniqueId) and (PlayerLocationSchema.name eq locationName) }
                    .firstOrNull() ?: PlayerLocationDb.new {}

                playerLocation.apply {
                    playerUuid = sender.uniqueId
                    name = locationName
                    world = sender.location.world.uid
                    x = sender.location.x
                    y = sender.location.y
                    z = sender.location.z
                }
            }

            sender.sendMessageUsingKey(
                Message.EssentialsWarpSet, mapOf(
                    "location-name" to Component.text(locationName, NamedTextColor.WHITE)
                )
            )
        }
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return listOf("<location-name>")
    }
}