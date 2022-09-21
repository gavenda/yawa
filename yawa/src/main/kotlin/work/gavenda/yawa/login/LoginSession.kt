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

package work.gavenda.yawa.login

import com.comphenix.protocol.wrappers.WrappedProfilePublicKey.WrappedProfileKeyData
import java.util.*

/**
 * Represents a login session.
 */
data class LoginSession(
    val name: String,
    val serverId: String,
    val verifyToken: ByteArray,
    val profileKeyData: Optional<WrappedProfileKeyData>,
    val uuid: Optional<UUID>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LoginSession

        if (name != other.name) return false
        if (serverId != other.serverId) return false
        if (uuid != other.uuid) return false
        if (!verifyToken.contentEquals(other.verifyToken)) return false
        if (profileKeyData != other.profileKeyData) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + serverId.hashCode()
        result = 31 * result + uuid.hashCode()
        result = 31 * result + verifyToken.contentHashCode()
        result = 31 * result + profileKeyData.hashCode()
        return result
    }
}