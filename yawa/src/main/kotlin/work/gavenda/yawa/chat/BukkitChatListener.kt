package work.gavenda.yawa.chat

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import work.gavenda.yawa.Config
import work.gavenda.yawa.api.placeholder.Placeholders
import work.gavenda.yawa.api.toLegacyText

class BukkitChatListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @Suppress("DEPRECATION")
    fun onPlayerChat(e: AsyncPlayerChatEvent) {
        val chatFormatComponent = Placeholders
            .withContext(e.player)
            .parse(Config.Chat.FormatMessage)
        val chatFormat = chatFormatComponent.toLegacyText()
        e.format = chatFormat.plus("%2\$s")
    }

}