package work.gavenda.yawa

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import work.gavenda.yawa.api.Command

class DisabledCommand : Command() {
    override fun execute(sender: CommandSender, args: Array<String>) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eThis feature is currently disabled."))
    }

    override fun onTab(sender: CommandSender, args: Array<String>): List<String>? {
        return null
    }
}
