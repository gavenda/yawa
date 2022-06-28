package work.gavenda.yawa.chunk

import org.bukkit.World
import org.bukkit.entity.Player
import work.gavenda.yawa.api.placeholder.PlaceholderProvider

class ChunkPlaceholder : PlaceholderProvider {

    companion object {
        const val CURRENT_CHUNK_X = "current-chunk-x"
        const val CURRENT_CHUNK_Z = "current-chunk-x"
    }

    override fun provideString(player: Player?, world: World?): Map<String, String?> {
        val currentChunk = player?.location?.chunk
        val currentChunkX = currentChunk?.x
        val currentChunkZ = currentChunk?.z

        return mapOf(
            CURRENT_CHUNK_X to currentChunkX.toString(),
            CURRENT_CHUNK_Z to currentChunkZ.toString()
        )
    }
}