package work.gavenda.yawa.skin

import org.bukkit.command.CommandSender
import work.gavenda.yawa.api.Command

class SkinCommand : Command("yawa.skin") {

    override fun execute(sender: CommandSender, args: Array<String>) {
    }

    override fun onTab(sender: CommandSender, args: Array<String>): List<String>? {
        return subCommandKeys.toList()
    }

}