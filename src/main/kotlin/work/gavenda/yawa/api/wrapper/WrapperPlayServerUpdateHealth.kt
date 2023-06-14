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

package work.gavenda.yawa.api.wrapper

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer

class WrapperPlayServerUpdateHealth : AbstractPacket(PacketContainer(type), type) {

    companion object {
        val type: PacketType = PacketType.Play.Server.UPDATE_HEALTH
    }

    fun writeHealth(value: Float) {
        handle.float.write(0, value)
    }

    fun writeFood(value: Int) {
        handle.integers.write(0, value)
    }

    fun writeFoodSaturation(value: Float) {
        handle.float.write(1, value)
    }

    init {
        handle.modifier.writeDefaults()
    }
}