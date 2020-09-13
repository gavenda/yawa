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
import com.comphenix.protocol.events.PacketContainer
import java.security.PublicKey

class WrapperLoginServerEncryptionBegin : AbstractPacket(PacketContainer(type), type) {

    init {
        handle.modifier.writeDefaults()
    }

    /**
     * Writes the server identifier.
     * @param value new value
     */
    fun writeServerId(value: String) {
        handle.strings.write(0, value)
    }

    /**
     * Writes the public key.
     * @param publicKey the public key
     */
    fun writePublicKey(publicKey: PublicKey) {
        handle.getSpecificModifier(PublicKey::class.java).write(0, publicKey)
    }

    /**
     * Writes the verify token
     * @param value new value
     */
    fun writeVerifyToken(value: ByteArray) {
        handle.byteArrays.write(0, value)
    }

    companion object {
        val type: PacketType = PacketType.Login.Server.ENCRYPTION_BEGIN
    }
}