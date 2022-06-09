package work.gavenda.yawa.chat

import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import work.gavenda.yawa.Config
import work.gavenda.yawa.api.compat.sendMessageCompat
import work.gavenda.yawa.api.placeholder.Placeholders

class BukkitChatListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @Suppress("DEPRECATION")
    fun onPlayerChat(e: AsyncPlayerChatEvent) {
        e.isCancelled = true

        val chatMessage = Placeholders
            .withContext(e.player)
            .parse(Config.Chat.FormatMessage)
            .append(Component.text(e.message))

        e.recipients.forEach {
            it.sendMessageCompat(chatMessage)
        }
    }

}