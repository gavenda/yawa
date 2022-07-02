/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (c) 2022 Gavenda <gavenda@disroot.org>
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

/**
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 <http:></http:>//dmulloy2.net>
 * Copyright (C) Kristian S. Strangeland
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package work.gavenda.yawa.api.wrapper

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.reflect.StructureModifier
import com.comphenix.protocol.utility.MinecraftReflection
import com.comphenix.protocol.wrappers.BukkitConverters
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode
import com.google.common.hash.Hashing
import org.bukkit.World
import java.util.*


/**
 * @since Minecraft 1.16.2
 */
@Suppress("UNCHECKED_CAST")
class WrapperPlayServerRespawn : AbstractPacket(PacketContainer(type), type) {
    fun writePosition() {
        handle.optionalStructures.write(0, Optional.empty())
    }

    /**
     * Write the resource key.
     * @param world new value
     */
    fun writeResourceKey(world: World) {
        val nmsWorld = BukkitConverters.getWorldConverter().getGeneric(world)
        val nmsWorldClass = MinecraftReflection.getNmsWorldClass()
        val localWorldKey = nmsWorldClass.getDeclaredField("I").apply {
            isAccessible = true
        }
        val resourceKeyClass = MinecraftReflection.getMinecraftLibraryClass("net.minecraft.resources.ResourceKey")
        val resourceKey = localWorldKey.get(nmsWorld)
        val resourceMod = handle.getSpecificModifier(resourceKeyClass) as StructureModifier<Any>

        // Write resource key
        resourceMod.write(1, resourceKey)
    }

    /**
     * Write dimension.
     * @param world new value
     */
    fun writeDimensionTypes(world: World) {
        val nmsWorld = BukkitConverters.getWorldConverter().getGeneric(world)
        val nmsWorldClass = MinecraftReflection.getNmsWorldClass()
        val localWorldKey = nmsWorldClass.getDeclaredField("D").apply {
            isAccessible = true
        }
        val resourceKeyClass = MinecraftReflection.getMinecraftLibraryClass("net.minecraft.resources.ResourceKey")
        val resourceKey = localWorldKey.get(nmsWorld)
        val resourceMod = handle.getSpecificModifier(resourceKeyClass) as StructureModifier<Any>

        // Write resource key
        resourceMod.write(0, resourceKey)
    }

    /**
     * Write previous game mode.
     * @param value new value
     */
    fun writePreviousGameMode(value: NativeGameMode) {
        handle.gameModes.write(1, value)
    }

    /**
     * Write game mode.
     * @param value new value
     */
    fun writeGameMode(value: NativeGameMode) {
        handle.gameModes.write(0, value)
    }

    /**
     * Write the world seed.
     * @param seed the un-hashed world seed
     */
    @Suppress("UnstableApiUsage")
    fun writeSeed(seed: Long) {
        handle.longs.write(0, Hashing.sha256().hashLong(seed).asLong())
    }

    /**
     * Write debug mode
     * @param value new value
     */
    fun writeIsDebug(value: Boolean) {
        handle.booleans.write(0, value)
    }

    fun writeIsWorldFlat(value: Boolean) {
        handle.booleans.write(1, value)
    }

    fun writeCopyMetadata(value: Boolean) {
        handle.booleans.write(2, value)
    }

    companion object {
        val type: PacketType = PacketType.Play.Server.RESPAWN
    }

    init {
        handle.modifier.writeDefaults()
        writePosition()
    }
}