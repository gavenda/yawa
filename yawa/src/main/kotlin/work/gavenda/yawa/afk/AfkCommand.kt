package work.gavenda.yawa.afk

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import work.gavenda.yawa.Config
import work.gavenda.yawa.api.*

class AfkCommand : Command("yawa.afk") {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) return

        sender.isAfk = true

        val message = Placeholder
            .withContext(sender)
            .parse(Config.Afk.EntryMessage)

        sender.world.broadcastMessageIf(message) {
            Config.Afk.MessageEnabled
        }
    }

    override fun onTab(sender: CommandSender, args: Array<String>): List<String>? {
        return null
    }

}