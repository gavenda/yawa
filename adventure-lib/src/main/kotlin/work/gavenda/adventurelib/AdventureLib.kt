package work.gavenda.adventurelib

import net.kyori.adventure.text.Component
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import work.gavenda.adventurelib.environment.BukkitEnvironment
import work.gavenda.adventurelib.environment.Environment
import work.gavenda.adventurelib.environment.PaperEnvironment

object AdventureLib {

    private val advEnvironment: Environment = try {
        Class.forName("com.destroystokyo.paper.PaperConfig")
        PaperEnvironment()
    } catch (e: ClassNotFoundException) {
        BukkitEnvironment()
    }

    fun CommandSender.sendMessageCompat(component: Component) {
        advEnvironment.sendMessage(this, component)
    }

    fun World.sendMessageCompat(component: Component) {
        advEnvironment.sendMessage(this, component)
    }

    fun World.sendActionBarCompat(component: Component) {
        advEnvironment.sendActionBar(this, component)
    }

    fun Player.sendPlayerListHeaderCompat(component: Component) {
        advEnvironment.setPlayerListHeader(this, component)
    }

    fun Player.sendPlayerListFooterCompat(component: Component) {
        advEnvironment.setPlayerListFooter(this, component)
    }

    fun Player.kickCompat(component: Component) {
        advEnvironment.kickPlayer(this, component)
    }

    fun Player.playerListNameCompat(component: Component?) {
        advEnvironment.setPlayerListName(this, component)
    }

}