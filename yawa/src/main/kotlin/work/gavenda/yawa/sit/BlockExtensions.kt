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
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import work.gavenda.yawa.Plugin

const val META_BLOCK_OCCUPIED = "BlockOccupied"
const val META_BLOCK_SITTING_PLAYER = "BlockSittingPlayer"

/**
 * The block's occupied state.
 * @return true if occupied by a player, otherwise false
 */
var Block.isOccupied
    get() = if (hasMetadata(META_BLOCK_OCCUPIED)) {
        getMetadata(META_BLOCK_OCCUPIED)
            .first { it.owningPlugin == Plugin.Instance }
            .asBoolean()
    } else false
    set(value) = setMetadata(META_BLOCK_OCCUPIED, FixedMetadataValue(Plugin.Instance, value))

/**
 * Returns the sitting player, will be null if there is no one.
 */
var Block.sittingPlayer: Player?
    get() = if (hasMetadata(META_BLOCK_SITTING_PLAYER)) {
        getMetadata(META_BLOCK_SITTING_PLAYER)
            .first { it.owningPlugin == Plugin.Instance }
            .value() as Player
    } else null
    set(value) = setMetadata(META_BLOCK_SITTING_PLAYER, FixedMetadataValue(Plugin.Instance, value))