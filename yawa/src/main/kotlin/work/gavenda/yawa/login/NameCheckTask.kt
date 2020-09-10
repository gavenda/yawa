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

package work.gavenda.yawa.login

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.entity.Player
import work.gavenda.yawa.api.mojang.MojangApi
import work.gavenda.yawa.api.mojang.RateLimitException
import work.gavenda.yawa.api.wrapper.WrapperLoginServerEncryptionBegin
import work.gavenda.yawa.logger
import java.security.PublicKey

class NameCheckTask(
    private val packetEvent: PacketEvent,
    private val player: Player,
    private val username: String,
    private val publicKey: PublicKey
) : Runnable {

    private val serverId = ""

    override fun run() {
        try {
            val premiumUuid = MojangApi.findUuidByUsername(username)

            if (premiumUuid != null) {
                requestPremiumLogin(username)
            } else {
                // player is not premium -> start a cracked session
                startCrackedSession()
            }

        } catch (ex: RateLimitException) {
        } finally {
            ProtocolLibrary.getProtocolManager()
                .asynchronousManager
                .signalPacketTransmission(packetEvent)
        }
    }

    private fun startCrackedSession() {
        val session = LoginSession(username, serverId, byteArrayOf())
        Session.cache(player.address, session)
    }

    private fun requestPremiumLogin(username: String) {
        val verifyToken = MinecraftEncryption.generateVerifyToken()

        try {
            val encryptionBegin = WrapperLoginServerEncryptionBegin().apply {
                writeServerId("")
                writePublicKey(publicKey)
                writeVerifyToken(verifyToken)
            }
            encryptionBegin.sendPacket(player)
        } catch (ex: Exception) {
            logger.warn("Cannot send encryption packet. Falling back to cracked login", ex)
            return
        }

        val session = LoginSession(username, serverId, verifyToken)

        Session.cache(player.address, session)

        // cancel only if the player has a paid account otherwise login as normal offline player
        synchronized(packetEvent.asyncMarker.processingLock) {
            packetEvent.isCancelled = true
        }
    }

}