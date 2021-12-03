package work.gavenda.yawa.chunk

import net.kyori.adventure.text.Component
import org.bukkit.World
import org.bukkit.entity.Player
import work.gavenda.yawa.api.PlaceholderProvider

class ChunkPlaceholder : PlaceholderProvider {

    override fun provide(player: Player?, world: World?): Map<String, Component?> {
        return mapOf()
    }

    override fun provideString(player: Player?, world: World?): Map<String, String?> {
        val currentChunk = player?.location?.chunk
        val currentChunkX = currentChunk?.x
        val currentChunkZ = currentChunk?.z

        return mapOf(
            "current-chunk-x" to currentChunkX.toString(),
            "current-chunk-z" to currentChunkZ.toString()
        )
    }
}