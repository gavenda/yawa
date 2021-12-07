package work.gavenda.yawa.login

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.World
import org.bukkit.entity.Player
import work.gavenda.yawa.api.placeholder.PlaceholderProvider
import work.gavenda.yawa.api.placeholder.provider.PlayerPlaceholderProvider

/**
 * Overrides player name.
 */
class PremiumPlaceholderProvider : PlaceholderProvider {

    override fun provide(player: Player?, world: World?): Map<String, Component?> {
        if (player != null) {
            val hover = HoverEvent.showText(
                Component.text("Verified Minecraft Account", NamedTextColor.GREEN)
            )

            if (player.isVerified) {
                return mapOf(
                    PlayerPlaceholderProvider.PLAYER_NAME to Component
                        .text(player.name, NamedTextColor.GOLD)
                        .hoverEvent(hover)
                )
            }
        }
        return mapOf()
    }

}