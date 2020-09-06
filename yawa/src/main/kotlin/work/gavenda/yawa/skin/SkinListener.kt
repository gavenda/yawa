package work.gavenda.yawa.skin

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.api.Plugin
import work.gavenda.yawa.api.applySkin
import work.gavenda.yawa.api.bukkitAsyncTask
import work.gavenda.yawa.api.restoreSkin

/**
 * Applies skin on player join.
 */
class SkinListener : Listener {

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        val onlineMode = e.player.server.onlineMode

        bukkitAsyncTask(Plugin.Instance) {
            transaction {
                val playerTexture = PlayerTexture.findById(player.uniqueId)

                if (playerTexture != null) {
                    // Existing texture on database, apply
                    player.applySkin(playerTexture.texture, playerTexture.signature)
                } else if (!onlineMode) {
                    // Server in offline mode
                    player.restoreSkin()
                }
            }
        }
    }

}