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
package work.gavenda.yawa.chunk

import org.bukkit.Location
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.logger

/**
 * Updates the following chunk to be marked as always loaded.
 */
fun updateChunkMark(location: Location, marked: Boolean) {
    val world = location.world
    val chunkX = location.chunk.x
    val chunkZ = location.chunk.z

    world.getChunkAtAsync(location).thenAccept {
        world.setChunkForceLoaded(chunkX, chunkZ, marked)
    }

    if (marked) {
        logger.info("Chunk ($chunkX, $chunkZ) is marked to keep running")
    } else {
        logger.info("Chunk ($chunkX, $chunkZ) is unmarked")
    }

    transaction {
        val worldChunk = WorldChunk.find(chunk(world.name, chunkX, chunkZ))
            .firstOrNull() ?: WorldChunk.new {
            name = world.name
            x = chunkX
            z = chunkZ
        }

        worldChunk.marked = marked
    }
}

fun chunk(name: String, x: Int, z: Int): Op<Boolean> {
    return (WorldChunkSchema.name eq name) and (WorldChunkSchema.x eq x) and (WorldChunkSchema.z eq z)
}