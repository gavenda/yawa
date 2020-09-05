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
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode
import com.google.common.hash.Hashing

/**
 * @since Minecraft 1.16.2
 */
class WrapperPlayServerRespawn : AbstractPacket(PacketContainer(type), type) {

    /**
     * Write dimension.
     * @param value new value
     */
    fun writeDimension(value: Int) {
        handle.dimensions.write(0, value)
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

    fun writeIsAlive(value: Boolean) {
        handle.booleans.write(2, value)
    }

    companion object {
        val type: PacketType = PacketType.Play.Server.RESPAWN
    }

    init {
        handle.modifier.writeDefaults()
    }
}