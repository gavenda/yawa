/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 *  Copyright (C) 2021 Gavenda <gavenda@disroot.org>
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
 *
 */

package work.gavenda.yawa.ender

import io.papermc.lib.PaperLib
import org.bukkit.Location
import org.bukkit.entity.Player
import work.gavenda.yawa.Message
import work.gavenda.yawa.sendMessageUsingKey
import java.util.*

class EnderTeleportTask(
    private val teleportingPlayers: Queue<Player>,
    private val location: Location
) : Runnable {
    override fun run() {
        while (teleportingPlayers.isNotEmpty()) {
            val player = teleportingPlayers.remove()

            // Teleport to damaging entity
            PaperLib.teleportAsync(player, location).thenRun {
                player.sendMessageUsingKey(Message.EnderBattleTeleport)
            }
        }
    }
}