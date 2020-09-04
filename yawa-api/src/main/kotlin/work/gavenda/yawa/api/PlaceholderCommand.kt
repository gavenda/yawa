package work.gavenda.yawa.api

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PlaceholderCommand : Command("yawa.api.placeholder") {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) return

        Placeholder
            .withContext(sender, sender.world)
            .asHelpList().forEach {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', it))
            }
    }

    override fun onTab(sender: CommandSender, args: Array<String>): List<String>? {
        return null
    }

}