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

package work.gavenda.yawa.sit

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.EquipmentSlot
import org.spigotmc.event.entity.EntityDismountEvent
import work.gavenda.yawa.api.compat.teleportAsyncCompat

/**
 * Listens for events related to sitting.
 */
class SitListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        if (player.location.y.isNaN()) {
            player.teleportAsyncCompat(player.world.spawnLocation).get()
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPlayerInteract(e: PlayerInteractEvent) {
        // Must right click a block
        if (e.action != Action.RIGHT_CLICK_BLOCK) return
        if (e.hand != EquipmentSlot.HAND) return

        val player = e.player
        val block = e.clickedBlock!!

        player.sit(block)
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onPlayerTeleport(e: PlayerTeleportEvent) {
        val player: Player = e.player
        if (player.isSitting) {
            player.standUpFromSit()
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player: Player = event.player
        if (player.isSitting) {
            player.standUpFromSit()
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onPlayerDeath(e: PlayerDeathEvent) {
        val player: Player = e.entity
        if (player.isSitting) {
            player.standUpFromSit()
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBlockBreak(e: BlockBreakEvent) {
        val block = e.block
        if (block.isOccupied) {
            block.sittingPlayer?.standUpFromSit()
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onExitVehicle(e: EntityDismountEvent) {
        if (e.entity !is Player) return
        val player = e.entity as Player

        if (player.isSitting) {
            player.standUpFromSit()
        }
    }
}