package work.gavenda.yawa.afk

import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import work.gavenda.yawa.Config
import work.gavenda.yawa.Plugin
import work.gavenda.yawa.api.Placeholder
import work.gavenda.yawa.api.broadcastMessageIf
import work.gavenda.yawa.api.isAfk

const val META_AFK_LAST = "AfkLast"

/**
 * The last time the player interacted with anything in-game.
 * @return last interact milliseconds
 */
var Player.lastInteractionMillis: Long
    get() = if (hasMetadata(META_AFK_LAST)) {
        getMetadata(META_AFK_LAST)
            .first { it.owningPlugin == Plugin.Instance }
            .asLong()
    } else System.currentTimeMillis()
    set(value) = setMetadata(META_AFK_LAST, FixedMetadataValue(Plugin.Instance, value))

/**
 * Fires a player interaction.
 */
fun Player.doInteract() {
    if (isAfk) {
        isAfk = false

        val message = Placeholder
            .withContext(this)
            .parse(Config.Afk.LeaveMessage)

        world.broadcastMessageIf(message) {
            Config.Afk.MessageEnabled
        }
    }

    lastInteractionMillis = System.currentTimeMillis()
}

/**
 * Clears the last interaction.
 */
fun Player.clearLastInteract() {
    removeMetadata(META_AFK_LAST, Plugin.Instance)
}