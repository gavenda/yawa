package work.gavenda.yawa.api

import com.comphenix.protocol.utility.MinecraftReflection
import com.comphenix.protocol.wrappers.BukkitConverters
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.World

/**
 * Retrieves debug mode status.
 * @return true if world is in debug mode
 */
val World.isDebugMode: Boolean
    get() {
        val worldClass: Class<*> = MinecraftReflection.getNmsWorldClass()
        val localWorldKey = worldClass.getDeclaredField("dimensionKey")
        localWorldKey.isAccessible = true
        val localDebugWorld = worldClass.getDeclaredField("debugWorld")
        localDebugWorld.isAccessible = true

        return localDebugWorld.getBoolean(BukkitConverters.getWorldConverter().getGeneric(this))
    }

/**
 * Broadcast a message to all players in this world.
 * @param message message to broadcast
 */
fun World.broadcastMessage(message: String, alternateChar: Char = '&') {
    players.forEach { player ->
        player.sendMessage(
            *TextComponent.fromLegacyText(
                ChatColor.translateAlternateColorCodes(alternateChar, message)
            )
        )
    }
}

/**
 * Broadcast an action bar message to all players in this world.
 * @param text text to broadcast
 */
fun World.broadcastActionBar(text: String) {
    players.forEach { player ->
        player.sendActionBar('&', text)
    }
}

/**
 * Broadcast a message to all players in this world.
 * Automatically converts color with the '&' character.
 * @param message  message to broadcast
 * @param condition condition if allowed to broadcast
 */
fun World.broadcastMessageIf(message: String, condition: () -> Boolean) {
    if (!condition()) return
    broadcastMessage(message)
}

/**
 * Broadcast an action bar text to all players in this world.
 * Automatically converts color with the '&' character.
 * @param text text to broadcast
 * @param condition condition if allowed to broadcast
 */
fun World.broadcastActionBarIf(text: String, condition: () -> Boolean) {
    if (!condition()) return
    broadcastActionBar(text)
}