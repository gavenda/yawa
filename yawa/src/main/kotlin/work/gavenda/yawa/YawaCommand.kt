package work.gavenda.yawa

import org.bukkit.command.CommandSender
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.sendWithColor

/**
 * Plugin main command.
 */
class YawaCommand : Command("yawa") {

    override fun execute(sender: CommandSender, args: Array<String>) {
    }

    override fun onTab(sender: CommandSender, args: Array<String>): List<String>? {
        return null
    }

}

/**
 * Reloads the plugin.
 */
class YawaReloadCommand: Command("yawa.reload") {
    override fun execute(sender: CommandSender, args: Array<String>) {
        Plugin.Instance.onDisable()
        Plugin.Instance.onEnable()

        sender.sendWithColor("&ePlugin reloaded.")
    }

    override fun onTab(sender: CommandSender, args: Array<String>): List<String>? {
        return null
    }
}

/**
 * Only reloads the plugin configuration.
 */
class YawaReloadConfigCommand: Command("yawa.reload.config") {
    override fun execute(sender: CommandSender, args: Array<String>) {
        Plugin.Instance.reloadConfig()
        Plugin.Instance.loadConfig()

        sender.sendWithColor("&ePlugin configuration reloaded.")
    }

    override fun onTab(sender: CommandSender, args: Array<String>): List<String>? {
        return null
    }
}