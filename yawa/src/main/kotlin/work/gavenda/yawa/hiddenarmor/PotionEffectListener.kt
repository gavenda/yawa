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
package work.gavenda.yawa.hiddenarmor

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.potion.PotionEffectType
import work.gavenda.yawa.plugin
import work.gavenda.yawa.scheduler

class PotionEffectListener : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onPlayerInvisibleEffect(event: EntityPotionEffectEvent) {
        if (event.entity !is Player) return
        if (event.newEffect?.type != PotionEffectType.INVISIBILITY) return
        val player = event.entity as Player

        scheduler.runTaskLater(plugin, { _ ->
            player.updateHiddenArmor()
        }, 2L)
    }
}