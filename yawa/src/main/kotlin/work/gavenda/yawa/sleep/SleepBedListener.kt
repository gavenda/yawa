package work.gavenda.yawa.sleep

import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBedEnterEvent
import org.bukkit.event.player.PlayerBedLeaveEvent
import work.gavenda.yawa.Config
import work.gavenda.yawa.Plugin
import work.gavenda.yawa.api.Placeholder
import work.gavenda.yawa.api.broadcastMessageIf

/**
 * Sleep feature bed listener.
 */
class SleepBedListener(
    private val plugin: Plugin,
    private val skippingWorlds: Set<World>
) : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onBedEnter(event: PlayerBedEnterEvent) {
        val world = event.bed.world
        val player = event.player

        if (event.bedEnterResult != PlayerBedEnterEvent.BedEnterResult.OK) return

        plugin.server.scheduler.runTaskAsynchronously(plugin) { _ ->
            val message = Placeholder
                .withContext(player, world)
                .parse(Config.Sleep.Chat.PlayerSleeping)

            world.broadcastMessageIf(message) { Config.Sleep.Chat.Enabled }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onBedLeave(event: PlayerBedLeaveEvent) {
        val world = event.bed.world
        val player = event.player

        if (world in skippingWorlds) return

        plugin.server.scheduler.runTaskAsynchronously(plugin) { _ ->
            val message = Placeholder
                .withContext(player, world)
                .parse(Config.Sleep.Chat.PlayerLeftBed)

            world.broadcastMessageIf(message) { Config.Sleep.Chat.Enabled }
        }
    }

}