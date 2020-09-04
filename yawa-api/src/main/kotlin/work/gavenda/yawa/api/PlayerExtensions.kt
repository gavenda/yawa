package work.gavenda.yawa.api

import com.comphenix.protocol.utility.MinecraftReflection
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction.ADD_PLAYER
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction.REMOVE_PLAYER
import com.comphenix.protocol.wrappers.WrappedGameProfile
import com.comphenix.protocol.wrappers.WrappedSignedProperty
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import work.gavenda.yawa.api.mojang.MOJANG_KEY_TEXTURES
import work.gavenda.yawa.api.wrapper.*
import java.lang.reflect.Field
import java.lang.reflect.Method

const val META_AFK = "Afk"

/**
 * AFK state.
 * @return true if afk otherwise false
 */
var Player.isAfk: Boolean
    get() = if (hasMetadata(META_AFK)) {
        getMetadata(META_AFK)
            .first { it.owningPlugin == Plugin.Instance }
            .asBoolean()
    } else false
    set(value) {
        setMetadata(META_AFK, FixedMetadataValue(Plugin.Instance, value))
    }

/**
 * Returns the player's current latency in milliseconds. Uses reflection to actually cast to CraftPlayer entity.
 */
val Player.latencyInMillis: Int
    get() {
        try {
            val getHandle: Method = MinecraftReflection.getCraftPlayerClass().getDeclaredMethod("getHandle")
            val entityPlayer: Any = getHandle.invoke(this)
            val ping: Field = entityPlayer.javaClass.getDeclaredField("ping")

            return ping.getInt(entityPlayer)
        } catch (e: Exception) {
            logger.error("Error retrieving latency: ${e.message}")
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
            logger.error("Error retrieving previous game mode", e.message)
        }

        return NativeGameMode.fromBukkit(gameMode)
    }

/**
 * Applies a skin to this player.
 * @param textureInfo json encoded texture
 * @param signature base64 string signature, if any
 */
fun Player.applySkin(textureInfo: String, signature: String = "") {
    try {
        val gameProfile = WrappedGameProfile.fromPlayer(this)
        val textureSignedProperty = WrappedSignedProperty.fromValues(MOJANG_KEY_TEXTURES, textureInfo, signature)

        // Clear and re-assign textures with valid signature
        gameProfile.properties.clear()
        gameProfile.properties.put(MOJANG_KEY_TEXTURES, textureSignedProperty)

        updateSkin()
    } catch (ex: Exception) {
        logger.error("Unable to apply skin", ex)
    }
}

/**
 * Does a refresh of the player, applying the currently set skin if changed.
 * Always called after [Player.applySkin].
 */
@Suppress("DEPRECATION")
fun Player.updateSkin() {
    val removeInfo = WrapperPlayServerPlayerInfo().apply {
        writeAction(REMOVE_PLAYER)
    }
    val addInfo = WrapperPlayServerPlayerInfo().apply {
        writeAction(ADD_PLAYER)
    }
    val respawn = WrapperPlayServerRespawn().apply {
        writeDimension(world.environment.id)
        writeGameMode(NativeGameMode.fromBukkit(gameMode))
        writePreviousGameMode(previousGameMode)
        writeSeed(world.seed)
        writeDebug(world.isDebugMode)
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
    val abilities = WrapperPlayServerAbilities().apply {
        writeInvulnerable(isInvulnerable)
        writeFlyingSpeed(flySpeed)
        writeFlying(isFlying)
        writeWalkingSpeed(walkSpeed)
        writeCanFly(allowFlight)
        writeCanInstantlyBuild(false)
    }

    bukkitTask(Plugin.Instance) {
        for (p in Bukkit.getOnlinePlayers()) {
            p.hidePlayer(Plugin.Instance, this)
            p.showPlayer(Plugin.Instance, this)
        }

        removeInfo.sendPacket(this)
        addInfo.sendPacket(this)
        respawn.sendPacket(this)
        position.sendPacket(this)
        slot.sendPacket(this)
        abilities.sendPacket(this)
        updateInventory()
    }
}