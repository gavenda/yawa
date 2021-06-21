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
import com.comphenix.protocol.wrappers.EnumWrappers

/**
 * @since Minecraft 1.17
 */
class WrapperPlayServerPosition : AbstractPacket(PacketContainer(type), type) {
    /**
     * Write X.
     * @param value new value
     */
    fun writeX(value: Double) {
        handle.doubles.write(0, value)
    }

    /**
     * Write Y.
     * @param value new value
     */
    fun writeY(value: Double) {
        handle.doubles.write(1, value)
    }

    /**
     * Write Z.
     * @param value new value
     */
    fun writeZ(value: Double) {
        handle.doubles.write(2, value)
    }

    /**
     * Write Yaw.
     * @param value new value
     */
    fun writeYaw(value: Float) {
        handle.float.write(0, value)
    }

    /**
     * Write Pitch.
     * @param value new value
     */
    fun writePitch(value: Float) {
        handle.float.write(1, value)
    }

    /**
     * Write on ground.
     * @param value new value
     */
    fun writeOnGround(value: Boolean) {
        handle.booleans.write(0, value)
    }

//    enum class PlayerTeleportFlag {
//        X, Y, Z, Y_ROT, X_ROT
//    }
//
//    private val flagsModifier: StructureModifier<Set<PlayerTeleportFlag>>
//        get() = handle.getSets(
//            EnumWrappers.getGenericConverter(flagsClass, PlayerTeleportFlag::class.java)
//        )
//
//    /**
//     * Write flags.
//     * @param value new value
//     */
//    fun writeFlags(value: Set<PlayerTeleportFlag>) {
//        flagsModifier.write(0, value)
//    }

    companion object {
        val type: PacketType = PacketType.Play.Server.POSITION
//        private val flagsClass = MinecraftReflection
//            .getMinecraftClass(
//                "EnumPlayerTeleportFlags",
//                "PacketPlayOutPosition\$EnumPlayerTeleportFlags"
//            )
    }

    init {
        handle.modifier.writeDefaults()
    }
}