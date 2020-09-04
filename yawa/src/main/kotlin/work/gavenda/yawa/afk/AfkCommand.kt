package work.gavenda.yawa.afk

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.Config
import work.gavenda.yawa.api.Placeholder
import work.gavenda.yawa.api.broadcastMessage
import work.gavenda.yawa.api.isAfk

class AfkCommand : Command("yawa.afk") {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) return

        sender.isAfk = true

        val message = Placeholder
            .withContext(sender)
            .parse(Config.Afk.EntryMessage)

        sender.world.broadcastMessage(message)
    }

    override fun onTab(sender: CommandSender, args: Array<String>): List<String>? {
        return null
    }

}