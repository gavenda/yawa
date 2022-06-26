package work.gavenda.yawa.chat

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import work.gavenda.yawa.Config
import work.gavenda.yawa.api.compat.sendMessageCompat
import work.gavenda.yawa.api.placeholder.Placeholders
import work.gavenda.yawa.api.toLegacyText
import work.gavenda.yawa.discord.DiscordFeature
import work.gavenda.yawa.server

class BukkitChatListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @Suppress("DEPRECATION")
    fun onPlayerChat(e: org.bukkit.event.player.AsyncPlayerChatEvent) {
        e.isCancelled = true

        val chatFormatComponent = Placeholders
            .withContext(e.player)
            .parse(Config.Chat.FormatMessage)
        val chatFormattedComponent = Placeholders
            .withContext(e.player)
            .parse(e.message)

        if(DiscordFeature.enabled) {
            DiscordFeature.sendMessage(e.player, chatFormattedComponent)
        }

        server.onlinePlayers.forEach { player ->
            player.sendMessageCompat(chatFormatComponent.append(chatFormattedComponent))
        }

    }

}