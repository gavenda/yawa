package work.gavenda.yawa.hiddenarmor

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.potion.PotionEffectType
import work.gavenda.yawa.plugin
import work.gavenda.yawa.scheduler

class PotionEffectListener : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onPlayerInvisibleEffect(event: EntityPotionEffectEvent) {
        if (event.entity !is Player) return
        if (event.newEffect?.type != PotionEffectType.INVISIBILITY) return
        val player = event.entity as Player

        scheduler.runTaskLater(plugin, { _ ->
            player.updateHiddenArmor()
        }, 2L)
    }
}