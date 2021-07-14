package work.gavenda.yawa.chunk

import io.papermc.lib.PaperLib
import org.bukkit.Location
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture

/**
 * Updates the following chunk to be marked as always loaded.
 */
fun updateChunkMark(location: Location, marked: Boolean): CompletableFuture<Void> {
    return PaperLib.getChunkAtAsync(location).thenAccept {
        val world = it.world
        val chunkX = it.x
        val chunkZ = it.z

        world.loadChunk(chunkX, chunkZ)
        world.setChunkForceLoaded(chunkX, chunkZ, marked)

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
}

fun chunk(name: String, x: Int, z: Int): Op<Boolean> {
    return (WorldChunkSchema.name eq name) and (WorldChunkSchema.x eq x) and (WorldChunkSchema.z eq z)
}