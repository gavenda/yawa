package work.gavenda.yawa.api

import com.comphenix.protocol.reflect.StructureModifier
import com.comphenix.protocol.utility.MinecraftReflection
import com.comphenix.protocol.wrappers.*
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction.ADD_PLAYER
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction.REMOVE_PLAYER
import org.bukkit.Bukkit
import org.bukkit.WorldType
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import work.gavenda.yawa.api.mojang.MOJANG_KEY_TEXTURES
import work.gavenda.yawa.api.wrapper.WrapperPlayServerHeldItemSlot
import work.gavenda.yawa.api.wrapper.WrapperPlayServerPlayerInfo
import work.gavenda.yawa.api.wrapper.WrapperPlayServerPosition
import work.gavenda.yawa.api.wrapper.WrapperPlayServerRespawn


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
            val getHandle = MinecraftReflection.getCraftPlayerClass().getDeclaredMethod("getHandle")
            val entityPlayer = getHandle.invoke(this)
            val ping = entityPlayer.javaClass.getDeclaredField("ping")

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

        if (!isDead) {
            updateSkin()
        }
    } catch (ex: Exception) {
        logger.error("Unable to apply skin", ex)
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
 * Always called after [Player.applySkin].
 */
@Suppress("UNCHECKED_CAST", "DEPRECATION")
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
        val nmsWorldClass = MinecraftReflection.getNmsWorldClass()
        val localWorldKey = nmsWorldClass.getDeclaredField("dimensionKey").apply {
            isAccessible = true
        }
        val resourceKeyClass = MinecraftReflection.getMinecraftClass("ResourceKey")
        val nmsWorld = BukkitConverters.getWorldConverter().getGeneric(world)
        val resourceKey = localWorldKey.get(nmsWorld)
        val resourceMod = handle.getSpecificModifier(resourceKeyClass) as StructureModifier<Any>

        // Write resource key
        resourceMod.write(resourceMod.size() - 1, resourceKey)

        writeDimension(world.environment.id)
        writeGameMode(NativeGameMode.fromBukkit(gameMode))
        writePreviousGameMode(previousGameMode)
        writeSeed(world.seed)
        writeIsDebug(world.isDebugMode)
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

    // Show update to other players
    bukkitTask(Plugin.Instance) {
        for (p in Bukkit.getOnlinePlayers()) {
            p.hidePlayer(Plugin.Instance, this)
            p.showPlayer(Plugin.Instance, this)
        }
    }
}