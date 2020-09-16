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

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.Stairs
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import work.gavenda.yawa.Config
import work.gavenda.yawa.Plugin
import work.gavenda.yawa.api.Placeholder
import work.gavenda.yawa.api.translateColorCodes

const val META_PLAYER_SITTING = "PlayerSitting"
const val MAX_SIT_DISTANCE = 2.0

/**
 * The player's sitting state.
 * @return true if sitting, otherwise false
 */
var Player.isSitting
    get() = if (hasMetadata(META_PLAYER_SITTING)) {
        getMetadata(META_PLAYER_SITTING)
            .first { it.owningPlugin == Plugin.Instance }
            .asBoolean()
    } else false
    set(value) = setMetadata(META_PLAYER_SITTING, FixedMetadataValue(Plugin.Instance, value))

/**
 * Checks if the player can currently sit at the given block.
 * @param block block to sit at
 */
fun Player.canSitAt(block: Block): Boolean {
    // Must not sneak
    if (isSneaking) return false
    // Must be nearby
    if (location.distance(block.location.add(0.5, 0.0, 0.5)) > MAX_SIT_DISTANCE) return false
    // Must have empty hand
    if (inventory.itemInMainHand.type != Material.AIR) return false
    // Must not be sneaking
    if (isSitting) return false
    // Block is not occupied
    if (block.isOccupied) return false

    return true
}

/**
 * Sit at the given block.
 * @param block the block to sit at
 * @return calculated location
 */
fun Player.sit(block: Block) {
    // Make sure we can sit
    if (canSitAt(block).not()) return
    // And only at stairs
    if (block.blockData !is Stairs) return

    val stairs = block.blockData as Stairs
    val sitHeight = 0.5

    // Make sure we can sit on it
    if (stairs.canSit.not()) return

    val ascendingFacing = stairs.facing
    val yaw = when (ascendingFacing.oppositeFace) {
        BlockFace.NORTH -> 180f
        BlockFace.EAST -> -90f
        BlockFace.SOUTH -> 0f
        BlockFace.WEST -> 90f
        else -> location.yaw
    }

    val facingLeft = ascendingFacing.rotateLeft()
    val facingRight = ascendingFacing.rotateRight()
    val widthLeft = ascendingFacing.calculateStairsWidth(block, facingLeft)
    val widthRight = ascendingFacing.calculateStairsWidth(block, facingRight)
    val widthSum = widthLeft + widthRight + 1
    if (widthSum > MAX_STAIRS_WIDTH) return

    val sitLocation = block.location

    sitLocation.yaw = yaw
    sitLocation.add(0.5, sitHeight - 0.5, 0.5)

    val chairEntity = sitLocation.spawnChairEntity()

    teleportAsync(sitLocation)
    chairEntity.addPassenger(this)
    block.sittingPlayer = this
    isSitting = true

    sendMessage(
        Placeholder.withContext(this)
            .parse(Config.Messages.PlayerSitStart)
            .translateColorCodes()
    )
}

/**
 * Stand up from sitting.
 */
fun Player.standUpFromSit() {
    val chairEntity = vehicle

    leaveVehicle()
    chairEntity?.remove()
    isSneaking = false
    isSitting = false

    sendMessage(
        Placeholder.withContext(this)
            .parse(Config.Messages.PlayerSitEnd)
            .translateColorCodes()
    )
}