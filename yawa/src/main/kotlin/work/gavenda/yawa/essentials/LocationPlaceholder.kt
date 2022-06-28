package work.gavenda.yawa.essentials

import org.bukkit.World
import org.bukkit.entity.Player
import work.gavenda.yawa.api.placeholder.PlaceholderProvider

class LocationPlaceholder : PlaceholderProvider {

    companion object {
        const val X = "location-x"
        const val Y = "location-y"
        const val Z = "location-z"
    }

    override fun provideString(player: Player?, world: World?): Map<String, String?> {
        val location = player?.location

        return mapOf(
            X to location?.blockX.toString(),
            Y to location?.blockY.toString(),
            Z to location?.blockZ.toString(),
        )
    }
}