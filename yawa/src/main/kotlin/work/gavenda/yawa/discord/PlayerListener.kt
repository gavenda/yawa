package work.gavenda.yawa.discord

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import work.gavenda.yawa.api.compat.deathMessageCompat
import work.gavenda.yawa.discordAlert
import work.gavenda.yawa.logger
import work.gavenda.yawa.sleep.sleepKicked

class PlayerListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerJoin(e: PlayerJoinEvent) {
        e.player.discordAlert("${e.player.name} joined the server", color = NamedTextColor.GREEN)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerLeave(e: PlayerQuitEvent) {
        if (e.player.sleepKicked) return
        e.player.discordAlert("${e.player.name} left the server", color = NamedTextColor.RED)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerAdvancement(e: PlayerAdvancementDoneEvent) {
        try {
            val craftAdvancement = (e.advancement as Any).javaClass.getMethod("getHandle").invoke(e.advancement)
            val advancementDisplay = craftAdvancement.javaClass.getMethod("c").invoke(craftAdvancement)
            val display = advancementDisplay.javaClass.getMethod("i").invoke(advancementDisplay) as Boolean
            if (!display) return
        } catch (e: NullPointerException) {
            return
        } catch (e: Exception) {
            logger.info("Failed to check if advancement should be displayed: $e")
        }

        e.player.discordAlert("${e.player.name} has made the advancement '${e.advancement.title}'")
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerDeath(e: PlayerDeathEvent) {
        e.deathMessageCompat?.let {
            e.entity.discordAlert(it, color = NamedTextColor.BLACK)
        }
    }
}