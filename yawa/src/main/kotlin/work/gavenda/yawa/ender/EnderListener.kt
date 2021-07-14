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

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.EnderDragon
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import work.gavenda.yawa.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Listens to a possible event of an ender dragon to be hit.
 */
class EnderListener(
    private val teleportingPlayers: Queue<Player>
) : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onEntityDamage(e: EntityDamageByEntityEvent) {
        val damager = e.damager
        val entity = e.entity

        // Damaging entity must be player or projectile
        if (damager !is Player && damager !is Projectile) return
        // If a projectile, shooter should be a player
        if (damager is Projectile && damager.shooter !is Player) return
        // Damaged entity must be ender
        if (entity !is EnderDragon) return

        val damagingPlayer = if (damager is Projectile) {
            (damager.shooter as Player)
        } else e.damager

        val location = damagingPlayer.location

        val players = Bukkit.getOnlinePlayers()
            .asSequence()
            // Don't include the damaging player
            .filter { it != damagingPlayer }
            // Don't include teleporting players
            .filter { teleportingPlayers.contains(it).not() }
            // Don't include the dead
            .filter { it.isDead.not() }
            // Don't include others who are already in the end
            .filter { it.world.environment != World.Environment.THE_END }

        // Empty, no one is on over world
        if (players.none()) return

        players.forEach { player ->
            if (teleportingPlayers.contains(player).not()) {
                teleportingPlayers.offer(player)
                player.sendMessageUsingKey(Message.EnderBattleStart)
            }
        }

        // Teleport to ender dragon after 5 seconds
        val secondsInTicks = TimeUnit.SECONDS.toTicks(5)
        val enderTeleportTask = EnderTeleportTask(teleportingPlayers, location)

        scheduler.runTaskLater(plugin, enderTeleportTask, secondsInTicks)
    }

}