package work.gavenda.yawa.afk

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.*

/**
 * The listeners necessary for an AFK system.
 */
class AfkListener : Listener {

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        e.player.doInteract()
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        e.player.clearLastInteract()
    }

    @EventHandler
    fun onPlayerChat(e: AsyncPlayerChatEvent) {
        e.player.doInteract()
    }

    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
        e.player.doInteract()
    }

    @EventHandler
    fun onPlayerAttack(e: EntityDamageByEntityEvent) {
        if (e.damager is Player) {
            (e.damager as Player).doInteract()
        }
    }

    @EventHandler
    fun onPlayerCommand(e: PlayerCommandPreprocessEvent) {
        e.player.doInteract()
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        e.player.doInteract()
    }

    @EventHandler
    fun onPlayerBlockPlace(e: BlockPlaceEvent) {
        e.player.doInteract()
    }

    @EventHandler
    fun onPlayerBlockBreak(e: BlockBreakEvent) {
        e.player.doInteract()
    }
}