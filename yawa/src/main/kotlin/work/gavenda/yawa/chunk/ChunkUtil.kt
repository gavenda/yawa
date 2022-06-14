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

    world.loadChunk(chunkX, chunkZ)
    world.setChunkForceLoaded(chunkX, chunkZ, marked)

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