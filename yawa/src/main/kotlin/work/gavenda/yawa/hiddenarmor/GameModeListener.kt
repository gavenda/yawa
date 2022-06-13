package work.gavenda.yawa.hiddenarmor

import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerGameModeChangeEvent
import work.gavenda.yawa.plugin
import work.gavenda.yawa.scheduler

class GameModeListener : Listener {
    @EventHandler
    fun onGameModeChange(event: PlayerGameModeChangeEvent) {
        if (!HiddenArmorFeature.hasPlayer(event.player)) return
        if (event.newGameMode == GameMode.CREATIVE) {
            HiddenArmorFeature.addIgnoredPlayer(event.player)
            event.player.updateHiddenArmor()
            HiddenArmorFeature.removeIgnoredPlayer(event.player)
        } else {
            scheduler.runTaskLater(plugin, { _ ->
                event.player.updateHiddenArmor()
            }, 1L)
        }
    }
}