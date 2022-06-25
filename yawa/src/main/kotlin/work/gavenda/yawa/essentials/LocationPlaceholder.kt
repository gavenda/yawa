package work.gavenda.yawa.essentials

import org.bukkit.World
import org.bukkit.entity.Player
import work.gavenda.yawa.api.placeholder.PlaceholderProvider

class LocationPlaceholder : PlaceholderProvider {

    override fun provideString(player: Player?, world: World?): Map<String, String?> {
        val location = player?.location

        return mapOf(
            "location-x" to location?.blockX.toString(),
            "location-y" to location?.blockY.toString(),
            "location-z" to location?.blockZ.toString(),
        )
    }
}