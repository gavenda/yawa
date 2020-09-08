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

class WrapperPlayServerAbilities : AbstractPacket(PacketContainer(type), type) {

    fun writeInvulnerable(value: Boolean) {
        handle.booleans.write(0, value)
    }

    fun writeFlying(value: Boolean) {
        handle.booleans.write(1, value)
    }

    fun writeCanFly(value: Boolean) {
        handle.booleans.write(2, value)
    }

    fun writeCanInstantlyBuild(value: Boolean) {
        handle.booleans.write(3, value)
    }

    fun writeFlyingSpeed(value: Float) {
        handle.float.write(0, value)
    }

    fun writeWalkingSpeed(value: Float) {
        handle.float.write(1, value)
    }

    companion object {
        val type: PacketType = PacketType.Play.Server.ABILITIES
    }

    init {
        handle.modifier.writeDefaults()
    }
}