/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (C) 2020 Gavenda <gavenda@disroot.org>
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

package work.gavenda.yawa.playerhead

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import work.gavenda.yawa.api.toTextComponent

class PlayerHeadListener : Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onEntityDeath(e: EntityDeathEvent) {
        val victim = e.entity
        val killer = victim.killer ?: return
        if (victim !is Player) return

        // Create player head
        val item = ItemStack(Material.PLAYER_HEAD)
        val skull = item.itemMeta as SkullMeta

        skull.owningPlayer = victim
        skull.lore = (listOf(
            "Trophy rewarded by yeeting ${victim.name} out of existence"
        ))
        item.itemMeta = skull

        // Drop the item
        killer.world.dropItemNaturally(victim.location, item)
    }

}