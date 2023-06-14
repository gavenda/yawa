/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (c) 2022-2023 Gavenda <gavenda@disroot.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package work.gavenda.yawa.api

import com.comphenix.protocol.injector.temporary.TemporaryPlayerFactory
import com.comphenix.protocol.reflect.FuzzyReflection
import com.comphenix.protocol.reflect.accessors.Accessors
import com.comphenix.protocol.utility.MinecraftReflection
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedGameProfile
import com.comphenix.protocol.wrappers.WrappedSignedProperty
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.metadata.FixedMetadataValue
import work.gavenda.yawa.api.compat.schedulerCompat
import work.gavenda.yawa.api.mojang.MOJANG_KEY_TEXTURES
import work.gavenda.yawa.api.wrapper.WrapperLoginServerDisconnect
import work.gavenda.yawa.logger
import work.gavenda.yawa.plugin
import java.util.*


const val META_AFK = "Afk"

/**
 * AFK state.
 * @return true if afk otherwise false
 */
var Player.afk: Boolean
    get() = if (hasMetadata(META_AFK)) {
        getMetadata(META_AFK)
            .first { it.owningPlugin == plugin }
            .asBoolean()
    } else false
    set(value) {
        setMetadata(META_AFK, FixedMetadataValue(plugin, value))
    }

/**
 * Returns the player's current latency in milliseconds. Uses reflection to actually cast to CraftPlayer entity.
 */
val Player.latencyInMillis: Int
    get() {
        val bukkitPlayer = Bukkit.getPlayer(uniqueId)
        return bukkitPlayer?.ping ?: 0
    }

/**
 * Applies a skin to this player and immediately reflects the changes in-game.
 * @param textureInfo json encoded texture
 * @param signature base64 string signature, if any
 */
fun Player.applySkin(textureInfo: String, signature: String = "") {
    // Should be done on main thread
    schedulerCompat.runAtNextTick(plugin) {
        val gameProfile = WrappedGameProfile.fromPlayer(this)
        val textureSignedProperty = WrappedSignedProperty.fromValues(MOJANG_KEY_TEXTURES, textureInfo, signature)

        gameProfile.properties.clear()
        gameProfile.properties.put(MOJANG_KEY_TEXTURES, textureSignedProperty)

        // Refresh
        Bukkit.getOnlinePlayers().forEach { player ->
            player.hidePlayer(plugin, this)
            player.showPlayer(plugin, this)
        }

        refreshPlayer()
        updateScaledHealth()
        exp = exp
        level = level
    }
}

/**
 * Update scaled health.
 */
fun Player.updateScaledHealth() {
    val updateScaledHealthMethod =
        MinecraftReflection.getCraftPlayerClass().getDeclaredMethod("updateScaledHealth")
    updateScaledHealthMethod.isAccessible = true
    updateScaledHealthMethod.invoke(this)
}

fun Player.refreshPlayer() {
    val refreshPlayerMethod = MinecraftReflection.getCraftPlayerClass().getDeclaredMethod("refreshPlayer")
    refreshPlayerMethod.isAccessible = true
    refreshPlayerMethod.invoke(this)
}

/**
 * Underlying network manager.
 */
val Player.networkManager: Any
    get() {
        val injectorContainer = TemporaryPlayerFactory.getInjectorFromPlayer(this);
        val injectorClass = Class.forName("com.comphenix.protocol.injector.netty.Injector");
        val rawInjector = FuzzyReflection.getFieldValue(injectorContainer, injectorClass, true)
        val rawInjectorClass = rawInjector.javaClass
        val accessor = Accessors.getFieldAccessorOrNull(rawInjectorClass, "networkManager", Any::class.java)
        return accessor.get(rawInjector)
    }

/**
 * Retrieves the spoofed uuid for this player.
 */
var Player.spoofedUuid: UUID
    get() {
        val managerClass = networkManager.javaClass
        val accessor = Accessors.getFieldAccessorOrNull(managerClass, "spoofedUUID", UUID::class.java)
        return accessor.get(networkManager) as UUID
    }
    set(value) {
        //https://github.com/bergerkiller/CraftSource/blob/master/net.minecraft.server/NetworkManager.java#L69
        val managerClass = networkManager.javaClass
        val accessor = Accessors.getFieldAccessorOrNull(managerClass, "spoofedUUID", UUID::class.java)
        return accessor.set(networkManager, value)
    }

/**
 * Disconnect the player using a packet.
 */
fun Player.disconnect(reason: String = "", cause: PlayerKickEvent.Cause = PlayerKickEvent.Cause.UNKNOWN) {
    // Must use main thread
    schedulerCompat.runAtNextTick(plugin) {
        logger.info("Packet disconnect: $reason")
        val disconnectPacket = WrapperLoginServerDisconnect().apply {
            writeReason(WrappedChatComponent.fromText(reason))
        }

        try {
            // Send disconnect packet
            disconnectPacket.sendPacket(this)
        } finally {
            try {
                kick(Component.text("Disconnected"), cause)
            } catch (e: UnsupportedOperationException) {
                // ProtocolLib will throw an error for kicking temporary players
            }
        }
    }
}