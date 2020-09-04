package work.gavenda.yawa.api.providers

import org.bukkit.World
import org.bukkit.entity.Player
import work.gavenda.yawa.api.PlaceholderProvider

/**
 * Provides common placeholders for world instances.
 */
class WorldPlaceholderProvider : PlaceholderProvider {
    override fun provide(player: Player?, world: World?): Map<String, String?> {
        return mapOf(
            "world-player-count" to world?.playerCount.toString(),
        )
    }
}