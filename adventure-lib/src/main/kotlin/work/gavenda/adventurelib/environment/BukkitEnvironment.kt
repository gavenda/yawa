package work.gavenda.adventurelib.environment

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BukkitEnvironment : Environment {

    private fun Component.toLegacyText(): String {
        return LegacyComponentSerializer.legacySection().serialize(this)
    }

    override fun sendMessage(sender: CommandSender, component: Component) {
        sender.sendMessage(component.toLegacyText())
    }

    override fun sendMessage(world: World, component: Component) {
        world.players.forEach {
            it.sendMessage(component.toLegacyText())
        }
    }

    @Suppress("DEPRECATION")
    override fun sendActionBar(world: World, component: Component) {
        val legacyComponent = TextComponent.fromLegacyText(component.toLegacyText())
        world.players.forEach { player ->
            player
                .spigot()
                .sendMessage(ChatMessageType.ACTION_BAR, *legacyComponent)
        }
    }

    @Suppress("DEPRECATION")
    override fun setPlayerListHeader(player: Player, component: Component) {
        player.playerListHeader = component.toLegacyText()
    }

    @Suppress("DEPRECATION")
    override fun setPlayerListFooter(player: Player, component: Component) {
        player.playerListFooter = component.toLegacyText()
    }

    @Suppress("DEPRECATION")
    override fun kickPlayer(player: Player, component: Component) {
        player.kickPlayer(component.toLegacyText())
    }

    @Suppress("DEPRECATION")
    override fun setPlayerListName(player: Player, component: Component?) {
        player.setPlayerListName(component?.toLegacyText())
    }
}
