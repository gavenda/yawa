package work.gavenda.yawa.api

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

/**
 * Utility send message with a default alternate char code of '&'.
 */
fun CommandSender.sendWithColor(text: String, alternateCharCode: Char = '&') {
    sendMessage(ChatColor.translateAlternateColorCodes(alternateCharCode, text))
}