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

import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.Stairs

const val MAX_STAIRS_WIDTH = 16

/**
 * Rotate to the left.
 */
fun BlockFace.rotateLeft(): BlockFace {
    return when (this) {
        BlockFace.NORTH -> BlockFace.WEST
        BlockFace.WEST -> BlockFace.SOUTH
        BlockFace.SOUTH -> BlockFace.EAST
        BlockFace.EAST -> BlockFace.NORTH
        else -> throw IllegalArgumentException("Cannot rotate block face $this")
    }
}

/**
 * Rotate to the right.
 */
fun BlockFace.rotateRight(): BlockFace {
    return when (this) {
        BlockFace.NORTH -> BlockFace.EAST
        BlockFace.EAST -> BlockFace.SOUTH
        BlockFace.SOUTH -> BlockFace.WEST
        BlockFace.WEST -> BlockFace.NORTH
        else -> throw IllegalArgumentException("Cannot rotate block face $this")
    }
}

/**
 * Calculate stairs width.
 * @param block start block
 * @param searchFace block face to search
 */
fun BlockFace.calculateStairsWidth(block: Block, searchFace: BlockFace): Int {
    var curBlock = block

    for (width in 0 until MAX_STAIRS_WIDTH) {
        val relativeBlock = curBlock.getRelative(searchFace)
        if (relativeBlock.blockData !is Stairs) return width
        val stairs = relativeBlock.blockData as Stairs
        if (stairs.canSit.not() || stairs.facing != this) {
            return width
        }

        curBlock = relativeBlock
    }

    return MAX_STAIRS_WIDTH
}