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

package work.gavenda.yawa.api

import org.bukkit.Location
import org.bukkit.util.Vector
import java.util.*

data class Region(
    val p1: BlockVector,
    val p2: BlockVector,
    val worldUniqueId: UUID
) {
    var min: Vector
    var max: Vector

    init {
        val tmp1 = Vector(p1.x, p1.y, p1.z)
        val tmp2 = Vector(p2.x, p2.y, p2.z)

        this.min = Vector.getMinimum(tmp1, tmp2)
        this.max = Vector.getMaximum(tmp1, tmp2)
    }
}

data class BlockVector(
    val x: Int,
    val y: Int,
    val z: Int
)

/**
 * Checks if the location is within the region.
 * @param region the region to check
 */
fun Location.within(region: Region): Boolean {
    val min = region.min
    val max = region.max

    if (!isWorldLoaded) {
        return false
    }
    if (world!!.uid != region.worldUniqueId) {
        return false
    }
    if (!(blockX >= min.blockX && blockX <= max.blockX)) {
        return false
    }
    if (!(blockZ >= min.blockZ && blockZ <= max.blockZ)) {
        return false
    }
    if (!(blockY >= min.blockY && blockY <= max.blockY)) {
        return false
    }

    return true
}