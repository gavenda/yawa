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
import com.comphenix.protocol.wrappers.BukkitConverters
import com.comphenix.protocol.wrappers.WrappedChatComponent
import work.gavenda.yawa.api.placeholder.Placeholders
import work.gavenda.yawa.api.toLegacyText
import work.gavenda.yawa.plugin
import work.gavenda.yawa.protocolManager
import work.gavenda.yawa.scheduler
import java.util.*

class ChatPreviewListener : PacketAdapter(
    params()
        .plugin(plugin)
        .listenerPriority(ListenerPriority.MONITOR)
        .types(PacketType.Play.Client.CHAT_PREVIEW, PacketType.Login.Client.START)
) {

    private fun isClientStart(event: PacketEvent): Boolean {
        if (event.packetType == PacketType.Login.Client.START) {
            event.packet.getOptionals(BukkitConverters.getWrappedPublicKeyDataConverter()).write(0, Optional.empty())
            return true
        }
        return false
    }

    override fun onPacketReceiving(event: PacketEvent) {
        if (event.isCancelled) return
        if (isClientStart(event)) return
        if (event.isPlayerTemporary) return

        val packet = event.packet
        val player = event.player
        val id = packet.integers.read(0)
        val chat = packet.strings.read(0)

        scheduler.runTaskAsynchronously(plugin) { _ ->
            val previewPacket = protocolManager.createPacket(PacketType.Play.Server.CHAT_PREVIEW)

            previewPacket.integers.write(0, id)

            val previewComponent = Placeholders.withContext(player).parse(chat)
            val previewLegacyText = previewComponent.toLegacyText()
            previewPacket.chatComponents.write(0, WrappedChatComponent.fromLegacyText(previewLegacyText))

            protocolManager.sendServerPacket(player, previewPacket)
        }
    }

    override fun onPacketSending(event: PacketEvent) {
        //do nothing
    }
}