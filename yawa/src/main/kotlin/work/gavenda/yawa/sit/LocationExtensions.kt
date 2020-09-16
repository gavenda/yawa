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

package work.gavenda.yawa.sit

import org.bukkit.Location
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Entity
import org.bukkit.util.Vector

/**
 * Spawns a chair entity at this location.
 * Used by the sit feature.
 */
fun Location.spawnChairEntity(): Entity {
    val arrow = world.spawnArrow(this, Vector(0, 1, 0), 0f, 0f)
    arrow.setGravity(false)
    arrow.isInvulnerable = true
    arrow.pickupStatus = AbstractArrow.PickupStatus.DISALLOWED
    return arrow
}