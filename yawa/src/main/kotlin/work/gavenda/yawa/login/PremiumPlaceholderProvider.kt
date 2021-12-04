package work.gavenda.yawa.login

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.World
import org.bukkit.entity.Player
import work.gavenda.yawa.api.PlaceholderProvider

class PremiumPlaceholderProvider : PlaceholderProvider {

    override fun provide(player: Player?, world: World?): Map<String, Component?> {
        if (player != null) {
            val hover = HoverEvent.showText(
                Component.text("Verified Minecraft Account", NamedTextColor.GREEN)
            )

            if (player.isVerified) {
                return mapOf(
                    "player-name" to Component.text(player.name, NamedTextColor.GOLD).hoverEvent(hover)
                )
            }
        }
        return mapOf()
    }

    override fun provideString(player: Player?, world: World?): Map<String, String?> {
        return mapOf()
    }

}