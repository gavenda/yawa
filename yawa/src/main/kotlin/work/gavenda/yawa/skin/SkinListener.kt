package work.gavenda.yawa.skin

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.api.Plugin
import work.gavenda.yawa.api.applySkin
import work.gavenda.yawa.api.bukkitAsyncTask

/**
 * Applies skin on player join.
 */
class SkinListener : Listener {

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player

        bukkitAsyncTask(Plugin.Instance) {
            transaction {
                val playerTexture = PlayerTexture.findById(player.uniqueId)

                if (playerTexture != null) {
                    player.applySkin(playerTexture.texture, playerTexture.signature)
                }
            }
        }
    }

}