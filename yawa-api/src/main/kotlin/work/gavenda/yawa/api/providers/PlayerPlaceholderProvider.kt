package work.gavenda.yawa.api.providers

import org.bukkit.World
import org.bukkit.entity.Player
import work.gavenda.yawa.api.PlaceholderProvider

class PlayerPlaceholderProvider: PlaceholderProvider {

    override fun provide(player: Player?, world: World?): Map<String, String?> {
        return mapOf(
            "player-name" to player?.name,
            "player-display-name" to player?.displayName
        )
    }

}