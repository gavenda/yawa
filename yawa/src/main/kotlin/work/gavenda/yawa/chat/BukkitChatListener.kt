package work.gavenda.yawa.chat

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import work.gavenda.yawa.Config
import work.gavenda.yawa.api.Placeholder

class BukkitChatListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @Suppress("DEPRECATION")
    fun onPlayerChat(e: org.bukkit.event.player.AsyncPlayerChatEvent) {
        val chatFormatComponent = Placeholder
            .withContext(e.player)
            .parse(Config.Chat.FormatMessage)
        val chatFormat = LegacyComponentSerializer.legacySection().serialize(chatFormatComponent)

        e.format = chatFormat.plus("%2\$s")
    }

}