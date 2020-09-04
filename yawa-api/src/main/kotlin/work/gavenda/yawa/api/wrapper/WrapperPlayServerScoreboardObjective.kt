/**
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 <http://dmulloy2.net>
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package work.gavenda.yawa.api.wrapper

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.reflect.IntEnum
import com.comphenix.protocol.wrappers.WrappedChatComponent

class WrapperPlayServerScoreboardObjective() : AbstractPacket(PacketContainer(type), type) {

    init {
        handle.modifier.writeDefaults()
    }

    /**
     * Enum containing all known packet modes.
     * @author dmulloy2
     */
    object Mode : IntEnum() {
        const val ADD_OBJECTIVE = 0
        const val REMOVE_OBJECTIVE = 1
        const val UPDATE_VALUE = 2
    }

    /**
     * Write name.
     * @param value new value
     */
    fun writeName(value: String) {
        handle.strings.write(0, value)
    }

    /**
     * Write objective display name.
     * @param value new value
     */
    fun writeDisplayName(value: WrappedChatComponent) {
        handle.chatComponents.write(0, value)
    }

    /**
     * Write health display.
     * @param value new value
     */
    fun writeHealthDisplay(value: HealthDisplay) {
        handle.getEnumModifier(HealthDisplay::class.java, 2).write(0, value)
    }

    /**
     * Write mode.
     * @param value new value
     */
    fun writeMode(value: Int) {
        handle.integers.write(0, value)
    }

    enum class HealthDisplay {
        INTEGER, HEARTS
    }

    companion object {
        val type: PacketType = PacketType.Play.Server.SCOREBOARD_OBJECTIVE
    }
}