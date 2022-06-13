/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 *  Copyright (C) 2021 Gavenda <gavenda@disroot.org>
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
 *
 */

package work.gavenda.yawa.api

import com.comphenix.protocol.injector.temporary.TemporaryPlayerFactory
import com.comphenix.protocol.reflect.FieldUtils
import com.comphenix.protocol.reflect.FuzzyReflection
import com.comphenix.protocol.utility.MinecraftReflection
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction.ADD_PLAYER
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction.REMOVE_PLAYER
import com.comphenix.protocol.wrappers.PlayerInfoData
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedGameProfile
import com.comphenix.protocol.wrappers.WrappedSignedProperty
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.WorldType
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import work.gavenda.yawa.api.compat.kickCompat
import work.gavenda.yawa.api.mojang.MOJANG_KEY_TEXTURES
import work.gavenda.yawa.api.wrapper.*
import java.util.*

const val META_AFK = "Afk"

/**
 * AFK state.
 * @return true if afk otherwise false
 */
var Player.isAfk: Boolean
    get() = if (hasMetadata(META_AFK)) {
        getMetadata(META_AFK)
            .first { it.owningPlugin == YawaAPI.Instance }
            .asBoolean()
    } else false
    set(value) {
        setMetadata(META_AFK, FixedMetadataValue(YawaAPI.Instance, value))
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
    Bukkit.getScheduler().runTask(YawaAPI.Instance) { _ ->
        val gameProfile = WrappedGameProfile.fromPlayer(this)
        val textureSignedProperty = WrappedSignedProperty.fromValues(MOJANG_KEY_TEXTURES, textureInfo, signature)

        // Clear and re-assign textures with valid signature
        gameProfile.properties.clear()
        gameProfile.properties.put(MOJANG_KEY_TEXTURES, textureSignedProperty)

        if (!isDead) {
            updateSkin()
        }
    }
}

fun Player.updateScaledHealth() {
    val updateScaledHealth = MinecraftReflection.getCraftPlayerClass().getDeclaredMethod("updateScaledHealth")
    updateScaledHealth.invoke(this)
}

/**
 * Does a refresh of the player, applying the currently set skin if changed.
 */
@Suppress("DEPRECATION", "UNUSED")
fun Player.updateSkin() {
    val wrappedGameProfile = WrappedGameProfile.fromPlayer(this)
    val enumGameMode = NativeGameMode.fromBukkit(gameMode)
    val displayName = WrappedChatComponent.fromText(playerListName)
    val playerInfoData = PlayerInfoData(wrappedGameProfile, 0, enumGameMode, displayName)

    val removeInfo = WrapperPlayServerPlayerInfo().apply {
        writeAction(REMOVE_PLAYER)
        writeData(listOf(playerInfoData))
    }
    val addInfo = WrapperPlayServerPlayerInfo().apply {
        writeAction(ADD_PLAYER)
        writeData(listOf(playerInfoData))
    }
    val respawn = WrapperPlayServerRespawn().apply {
        writeResourceKey(world)
        writeDimensionTypes(world)
        writeGameMode(NativeGameMode.fromBukkit(gameMode))
        writePreviousGameMode(NativeGameMode.fromBukkit(previousGameMode ?: gameMode))
        writeSeed(world.seed)
        writeIsDebug(world.debugMode)
        writeIsWorldFlat(world.worldType == WorldType.FLAT)
        // true = teleport like, false = player actually died
        writeCopyMetadata(true)
    }
    val position = WrapperPlayServerPosition().apply {
        writeX(location.x)
        writeY(location.y)
        writeZ(location.z)
        writeYaw(location.yaw)
        writePitch(location.pitch)

        // send an invalid teleport id in order to let Bukkit ignore the incoming confirm packet
        handle.integers.write(0, -1337)
    }

    // Refresh
    // - removePlayer
    // - addPlayer
    // - respawn
    // - updateAbilities
    // - position
    // - slot
    // - updateScaledHealth
    // - updateInventory
    // - triggerHealthUpdate

    // Send
    removeInfo.sendPacket(this)
    addInfo.sendPacket(this)
    respawn.sendPacket(this)
    position.sendPacket(this)

    // trigger update exp
    exp = exp
    // triggers updateAbilities
    walkSpeed = walkSpeed
    // update inventory
    updateInventory()
    // update held item
    inventory.heldItemSlot = inventory.heldItemSlot
    // update scaled health
    updateScaledHealth()

    // Show update to other players
    for (p in Bukkit.getOnlinePlayers()) {
        p.hidePlayer(YawaAPI.Instance, this)
        p.showPlayer(YawaAPI.Instance, this)
    }
}

/**
 * Underlying network manager.
 */
val Player.networkManager: Any
    get() {
        val socketInjector = TemporaryPlayerFactory.getInjectorFromPlayer(player)
        val injectorClass = Class.forName("com.comphenix.protocol.injector.netty.Injector")
        val rawInjector = FuzzyReflection.getFieldValue(socketInjector, injectorClass, true)
        return FieldUtils.readField(rawInjector, "networkManager", true)
    }

/**
 * Retrieves the spoofed uuid for this player.
 */
var Player.spoofedUuid: UUID
    get() = FieldUtils.readField(networkManager, "spoofedUUID", true) as UUID
    set(value) {
        //https://github.com/bergerkiller/CraftSource/blob/master/net.minecraft.server/NetworkManager.java#L69
        FieldUtils.writeField(networkManager, "spoofedUUID", value, true)
    }

/**
 * Returns this player instance as audience.
 */
fun Player.asAudience(): Audience {
    return YawaAPI.Instance.adventure.player(this)
}

/**
 * Disconnect the player using a packet.
 */
fun Player.disconnect(reason: String = "") {
    // Must use main thread
    Bukkit.getScheduler().runTask(YawaAPI.Instance) { _ ->
        apiLogger.info("Packet disconnect: $reason")
        val disconnectPacket = WrapperLoginServerDisconnect().apply {
            writeReason(WrappedChatComponent.fromText(reason))
        }

        try {
            // Send disconnect packet
            disconnectPacket.sendPacket(this)
        } finally {
            try {
                kickCompat(Component.text("Disconnected"))
            } catch (e: UnsupportedOperationException) {
                // ProtocolLib will throw an error for kicking temporary players
            }
        }
    }
}