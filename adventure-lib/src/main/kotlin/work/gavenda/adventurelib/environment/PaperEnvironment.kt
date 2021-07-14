package work.gavenda.adventurelib.environment

import net.kyori.adventure.text.Component
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PaperEnvironment : Environment {

    override fun sendMessage(sender: CommandSender, component: Component) {
        sender.sendMessage(component)
    }

    override fun sendMessage(world: World, component: Component) {
        world.sendMessage(component)
    }

    override fun sendActionBar(world: World, component: Component) {
        world.sendActionBar(component)
    }

    override fun setPlayerListHeader(player: Player, component: Component) {
        player.sendPlayerListHeader(component)
    }

    override fun setPlayerListFooter(player: Player, component: Component) {
        player.sendPlayerListFooter(component)
    }

    override fun kickPlayer(player: Player, component: Component) {
        player.kick(component)
    }

    override fun setPlayerListName(player: Player, component: Component?) {
        player.playerListName(component)
    }
}