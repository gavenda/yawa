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
        .optionAsync()
) {

    private fun isClientStart(event: PacketEvent): Boolean {
        if (event.packetType == PacketType.Login.Client.START) {
            event.packet.modifier.write(1, Optional.empty<Any>());
            return true
        }
        return false
    }

    override fun onPacketReceiving(event: PacketEvent) {
        if (event.isPlayerTemporary || event.isCancelled) return
        if (isClientStart(event)) return

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
}