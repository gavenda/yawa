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
 * along with this program.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package work.gavenda.yawa.api.wrapper

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.google.common.base.Objects
import org.bukkit.entity.Player
import java.lang.reflect.InvocationTargetException

/**
 * Constructs a new strongly typed wrapper for the given packet.
 *
 * @param handle - handle to the raw packet data.
 * @param type - the packet type.
 */
abstract class AbstractPacket(val handle: PacketContainer, type: PacketType) {
    /**
     * Send the current packet to the given receiver.
     *
     * @param receiver - the receiver.
     * @throws RuntimeException If the packet cannot be sent.
     */
    fun sendPacket(receiver: Player) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(
                receiver,
                handle
            )
        } catch (e: InvocationTargetException) {
            throw RuntimeException("Cannot send packet.", e)
        }
    }

    /**
     * Send the current packet to all online players.
     */
    fun broadcastPacket() {
        ProtocolLibrary.getProtocolManager().broadcastServerPacket(handle)
    }

    init {
        // Make sure we're given a valid packet
        require(Objects.equal(handle.type, type)) {
            "${handle.handle} is not a packet of type: $type"
        }
    }
}