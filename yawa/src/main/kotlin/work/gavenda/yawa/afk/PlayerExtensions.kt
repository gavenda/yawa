package work.gavenda.yawa.afk

import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import work.gavenda.yawa.Config
import work.gavenda.yawa.Plugin
import work.gavenda.yawa.api.*

/**
 * The last time the player interacted with anything in-game.
 * @return last interact milliseconds
 */
var Player.lastInteractionMillis: Long
    get() = if (hasMetadata(META_AFK_START)) {
        getMetadata(META_AFK_START)[0].asLong()
    } else System.currentTimeMillis()
    set(value) = setMetadata(META_AFK_START, FixedMetadataValue(Plugin.Instance, value))

/**
 * Fires a player interaction.
 */
fun Player.doInteract() {
    if (isAfk) {
        isAfk = false

        val message = Placeholder
            .withContext(this)
            .parse(Config.Afk.LeaveMessage)

        world.broadcastMessage(message)
    }

    lastInteractionMillis = System.currentTimeMillis()
}

fun Player.clearLastInteract() {
    removeMetadata(META_AFK_START, Plugin.Instance)
}