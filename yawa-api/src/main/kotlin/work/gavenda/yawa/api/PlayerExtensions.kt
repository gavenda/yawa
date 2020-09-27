/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (C) 2020 Gavenda <gavenda@disroot.org>
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

import com.comphenix.protocol.injector.server.TemporaryPlayerFactory
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
import org.bukkit.Bukkit
import org.bukkit.WorldType
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
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
        try {
            val getHandle = MinecraftReflection.getCraftPlayerClass().getDeclaredMethod("getHandle")
            val entityPlayer = getHandle.invoke(this)
            val ping = entityPlayer.javaClass.getDeclaredField("ping")

            return ping.getInt(entityPlayer)
        } catch (e: Exception) {
            apiLogger.error("Error retrieving latency: ${e.message}")
        }

        return 0
    }

/**
 * Retrieves this player previous game mode.
 */
val Player.previousGameMode: NativeGameMode
    get() {
        try {
            val entityPlayerClass: Class<*> = MinecraftReflection.getEntityPlayerClass()
            val playerInteractManager: Class<*> = MinecraftReflection.getMinecraftClass("PlayerInteractManager")
            val localHandleMethod = MinecraftReflection.getCraftPlayerClass().getDeclaredMethod("getHandle")
            val localInteractionField = entityPlayerClass.getDeclaredField("playerInteractManager")
            localInteractionField.isAccessible = true
            val localGameMode = playerInteractManager.getDeclaredField("e")
            localGameMode.isAccessible = true

            val nmsPlayer: Any = localHandleMethod.invoke(this)
            val interactionManager: Any = localInteractionField.get(nmsPlayer)
            val gameMode = localGameMode.get(interactionManager) as Enum<*>
            return NativeGameMode.valueOf(gameMode.name)
        } catch (e: Exception) {
            apiLogger.error("Error retrieving previous game mode", e.message)
        }

        return NativeGameMode.fromBukkit(gameMode)
    }

/**
 * Applies a skin to this player and immediately reflects the changes in-game.
 * @param textureInfo json encoded texture
 * @param signature base64 string signature, if any
 */
fun Player.applySkin(textureInfo: String, signature: String = "") {
    bukkitTask(YawaAPI.Instance) {
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

fun Player.updateAbilities() {
    val getHandle = MinecraftReflection.getCraftPlayerClass().getDeclaredMethod("getHandle")
    val entityPlayer = getHandle.invoke(this)
    val updateAbilities = entityPlayer.javaClass.getDeclaredMethod("updateAbilities")

    updateAbilities.invoke(entityPlayer)
}

fun Player.updateScaledHealth() {
    val updateScaledHealth = MinecraftReflection.getCraftPlayerClass().getDeclaredMethod("updateScaledHealth")
    updateScaledHealth.invoke(this)
}

fun Player.triggerHealthUpdate() {
    val getHandle = MinecraftReflection.getCraftPlayerClass().getDeclaredMethod("getHandle")
    val entityPlayer = getHandle.invoke(this)
    val updateAbilities = entityPlayer.javaClass.getDeclaredMethod("triggerHealthUpdate")

    updateAbilities.invoke(entityPlayer)
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
        writeDimension(world.environment.id)
        writeGameMode(NativeGameMode.fromBukkit(gameMode))
        writePreviousGameMode(previousGameMode)
        writeSeed(world.seed)
        writeIsDebug(world.debugMode)
        writeIsWorldFlat(world.worldType == WorldType.FLAT)
        // true = teleport like, false = player actually died
        writeIsAlive(true)
    }
    val position = WrapperPlayServerPosition().apply {
        writeX(location.x)
        writeY(location.y)
        writeZ(location.z)
        writeYaw(location.yaw)
        writePitch(location.pitch)
        writeFlags(emptySet())
    }
    val slot = WrapperPlayServerHeldItemSlot().apply {
        writeSlot(inventory.heldItemSlot)
    }

    // Show update to other players
    bukkitTask(YawaAPI.Instance) {
        for (p in Bukkit.getOnlinePlayers()) {
            p.hidePlayer(YawaAPI.Instance, this)
            p.showPlayer(YawaAPI.Instance, this)
        }
    }

    // Send self
    removeInfo.sendPacket(this)
    addInfo.sendPacket(this)
    respawn.sendPacket(this)
    updateAbilities()
    position.sendPacket(this)
    slot.sendPacket(this)
    updateScaledHealth()
    updateInventory()
    triggerHealthUpdate()

    // Op refresh
    if (this.isOp) {
        this.isOp = false
        this.isOp = true
    }
}

/**
 * Underlying network manager.
 */
val Player.networkManager: Any
    get() {
        val injectorContainer = TemporaryPlayerFactory.getInjectorFromPlayer(player)
        val injectorClass = Class.forName("com.comphenix.protocol.injector.netty.Injector")
        val rawInjector = FuzzyReflection.getFieldValue(injectorContainer, injectorClass, true)
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
 * Disconnect the player using a packet.
 */
fun Player.disconnect(reason: String = "") {
    apiLogger.info("Packet disconnect: $reason")
    val disconnectPacket = WrapperLoginServerDisconnect().apply {
        writeReason(WrappedChatComponent.fromText(reason))
    }
    // Send disconnect packet
    disconnectPacket.sendPacket(this)
    // Server cleanup
    kickPlayer("Disconnected")
}