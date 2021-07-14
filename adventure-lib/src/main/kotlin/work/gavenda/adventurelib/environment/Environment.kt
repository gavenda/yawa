package work.gavenda.adventurelib.environment

import net.kyori.adventure.text.Component
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

interface Environment {
    fun sendMessage(sender: CommandSender, component: Component)
    fun sendMessage(world: World, component: Component)
    fun sendActionBar(world: World, component: Component)
    fun setPlayerListHeader(player: Player, component: Component)
    fun setPlayerListFooter(player: Player, component: Component)
    fun kickPlayer(player: Player, component: Component)
    fun setPlayerListName(player: Player, component: Component?)

}