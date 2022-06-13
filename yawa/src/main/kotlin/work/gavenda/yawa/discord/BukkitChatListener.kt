package work.gavenda.yawa.discord

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import work.gavenda.yawa.Config
import work.gavenda.yawa.api.placeholder.Placeholders
import work.gavenda.yawa.api.toLegacyText

class BukkitChatListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("DEPRECATION")
    fun onPlayerChat(e: org.bukkit.event.player.AsyncPlayerChatEvent) {
        DiscordFeature.sendMessage(e.player, e.message)
    }

}