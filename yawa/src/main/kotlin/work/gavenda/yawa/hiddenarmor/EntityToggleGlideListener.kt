package work.gavenda.yawa.hiddenarmor

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityToggleGlideEvent
import work.gavenda.yawa.plugin
import work.gavenda.yawa.scheduler

class EntityToggleGlideListener : Listener {
    @EventHandler
    fun onPlayerToggleGlide(e: EntityToggleGlideEvent) {
        val player = e.entity
        if (player !is Player) return
        if (!HiddenArmorFeature.hasPlayer(player)) return

        scheduler.runTaskLater(plugin, { _ ->
            player.updateHiddenArmor()
        }, 1L)
    }
}