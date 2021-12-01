package work.gavenda.yawa.afk

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class BukkitAfkListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("DEPRECATION")
    fun onPlayerChat(e: org.bukkit.event.player.AsyncPlayerChatEvent) {
        e.player.doInteract()
    }
}