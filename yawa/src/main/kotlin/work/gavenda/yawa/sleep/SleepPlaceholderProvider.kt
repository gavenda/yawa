package work.gavenda.yawa.sleep

import org.bukkit.World
import org.bukkit.entity.Player
import work.gavenda.yawa.api.PlaceholderProvider

/**
 * Providers placeholders for the sleep feature.
 */
class SleepPlaceholderProvider : PlaceholderProvider {

    override fun provide(player: Player?, world: World?): Map<String, String> {
        return mapOf(
            "world-sleeping" to world?.sleeping?.size.toString(),
            "world-sleeping-needed" to world?.sleepingNeeded.toString()
        )
    }
}