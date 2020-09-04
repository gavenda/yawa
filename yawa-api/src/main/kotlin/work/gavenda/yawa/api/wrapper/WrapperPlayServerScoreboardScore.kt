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
import com.comphenix.protocol.wrappers.EnumWrappers.ScoreboardAction

class WrapperPlayServerScoreboardScore() : AbstractPacket(PacketContainer(type), type) {
    init {
        handle.modifier.writeDefaults()
    }

    /**
     * Write score name.
     * @param value new value
     */
    fun writeScoreName(value: String) {
        handle.strings.write(0, value)
    }

    /**
     * Write objective name.
     * @param value new value
     */
    fun writeObjectiveName(value: String) {
        handle.strings.write(1, value)
    }

    /**
     * Write value.
     * @param value new value
     */
    fun writeValue(value: Int) {
        handle.integers.write(0, value)
    }

    /**
     * Write scoreboard action.
     * @param value new value
     */
    fun writeScoreboardAction(value: ScoreboardAction) {
        handle.scoreboardActions.write(0, value)
    }

    companion object {
        val type: PacketType = PacketType.Play.Server.SCOREBOARD_SCORE
    }
}