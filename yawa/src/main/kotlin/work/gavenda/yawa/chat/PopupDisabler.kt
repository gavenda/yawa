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

package work.gavenda.yawa.chat

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.Converters
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedSaltedSignature
import work.gavenda.yawa.api.placeholder.Placeholders
import work.gavenda.yawa.api.toLegacyText
import work.gavenda.yawa.plugin
import work.gavenda.yawa.protocolManager
import work.gavenda.yawa.scheduler
import java.util.*

class PopupDisabler : PacketAdapter(
    params()
        .plugin(plugin)
        .listenerPriority(ListenerPriority.MONITOR)
        .types(PacketType.Play.Server.SERVER_DATA, PacketType.Play.Server.CHAT)
) {

    override fun onPacketSending(event: PacketEvent) {
        if (event.packetType == PacketType.Play.Server.SERVER_DATA) {
            event.packet.booleans.write(1, true)
        }
//        if (event.packetType == PacketType.Play.Server.CHAT) {
//            event.packet.signatures.write(0, WrappedSaltedSignature(0, byteArrayOf()))
//        }
    }
}