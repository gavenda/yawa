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

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.plugin.Plugin
import work.gavenda.yawa.Message
import work.gavenda.yawa.Messages
import work.gavenda.yawa.api.disconnect
import work.gavenda.yawa.logger
import work.gavenda.yawa.scheduler
import java.security.KeyPair

/**
 * Listens to packet encryption requests and determine whether it is valid.
 * Basically checks if you are actually the owner of the said account.
 */
class LoginEncryptionListener(
    plugin: Plugin,
    private val keyPair: KeyPair
) : PacketAdapter(
    params()
        .plugin(plugin)
        .types(PacketType.Login.Client.ENCRYPTION_BEGIN)
        .optionAsync()
) {

    override fun onPacketReceiving(packetEvent: PacketEvent) {
        val sharedSecret = packetEvent.packet.byteArrays.read(0).copyOf()
        // 1.19.3 disabled this
        // val either = packetEvent.packet.loginSignatures.read(0)
        // val signatureData = either.right().get()
        // val salt = signatureData.salt
        // val signature = signatureData.signature
        val player = packetEvent.player

        packetEvent.asyncMarker.incrementProcessingDelay()

        val session = Session.find(player.address!!)
        if (session == null) {
            player.disconnect(
                Messages
                    .forPlayer(player)
                    .get(Message.LoginInvalidRequest)
            )
            logger.warn("Attempted to send encryption response at an invalid state")
            return
        }

        val proceed: () -> Unit = {
            val encryptionTask = LoginEncryptionTask(
                packetEvent = packetEvent,
                session = session,
                player = player,
                keyPair = keyPair,
                sharedSecret = sharedSecret,
            )

            packetEvent.asyncMarker.incrementProcessingDelay()

            scheduler.runTaskAsynchronously(plugin, encryptionTask)
        }

        // if (session.profileKeyData.isEmpty) {
        proceed()
        // }
        // else if (session.profileKeyData.isPresent) {
        //    val publicKey = session.profileKeyData.get().key
        //
        //    if (MinecraftEncryption.verifySignedNonce(session.verifyToken, publicKey, salt, signature)) {
        //        proceed()
        //    } else {
        //        player.disconnect(
        //            Messages
        //                .forPlayer(player)
        //                .get(Message.LoginInvalidSignature)
        //        )
        //    }
        //}
    }
}