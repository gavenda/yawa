package work.gavenda.yawa.chat

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import work.gavenda.yawa.Config
import work.gavenda.yawa.api.placeholder.Placeholder
import work.gavenda.yawa.api.toLegacyText

class BukkitChatListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @Suppress("DEPRECATION")
    fun onPlayerChat(e: org.bukkit.event.player.AsyncPlayerChatEvent) {
        val chatFormatComponent = Placeholder
            .withContext(e.player)
            .parse(Config.Chat.FormatMessage)
        val chatFormat = chatFormatComponent.toLegacyText()

        e.format = chatFormat.plus("%2\$s")
    }

}