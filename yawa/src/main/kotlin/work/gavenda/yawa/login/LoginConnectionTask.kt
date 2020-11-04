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
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.api.mojang.MojangApi
import work.gavenda.yawa.api.mojang.RateLimitException
import work.gavenda.yawa.api.wrapper.WrapperLoginServerEncryptionBegin
import work.gavenda.yawa.logger
import java.security.KeyPair
import java.security.PublicKey

/**
 * Determines if you have a cracked account and begins encrypting your connection if possible.
 */
class LoginConnectionTask(
    private val packetEvent: PacketEvent,
    private val player: Player,
    private val name: String,
    private val keyPair: KeyPair
) : Runnable {

    private val serverId = ""
    private val protocolManager = ProtocolLibrary.getProtocolManager()

    override fun run() {
        try {
            val uuid = name.minecraftOfflineUuid()

            // Try getting information from database
            val userLogin = transaction {
                PlayerLogin.findById(uuid)
            }

            if (userLogin != null) {
                logger.info("User information already exists")
                if (userLogin.premium) {
                    encryptConnection(packetEvent, player, name, keyPair.public)
                } else {
                    unsecureConnection(player, name)
                }
            } else if (Session.hasPendingSession(player.address, name)) {
                logger.info("Pending session for $name")
                // Player has pending session, must have failed first premium attempt -> start an offline session
                unsecureConnection(player, name)
            } else {
                logger.info("Looking up premium uuid for player $name")
                // Contact Mojang API
                val premiumUuid = MojangApi.findUuidByName(name)

                if (premiumUuid != null) {
                    logger.info("Premium uuid found")
                    // Player is premium, encrypt connection
                    encryptConnection(packetEvent, player, name, keyPair.public)
                } else {
                    logger.info("Cannot find a premium uuid for player $name")
                    // Player is not premium -> start an offline session
                    unsecureConnection(player, name)
                }
            }
        } catch (ex: RateLimitException) {
            logger.warn("Cannot retrieve premium uuid, we have been rate-limited by the Mojang API", ex)
        } finally {
            protocolManager
                .asynchronousManager
                .signalPacketTransmission(packetEvent)
        }
    }

    /**
     * Begin an unsecure connection.
     */
    private fun unsecureConnection(player: Player, playerName: String) {
        logger.info("Initiating unsecure connection for player $playerName")

        val session = LoginSession(playerName, serverId, byteArrayOf())
        Session.cache(player.address, session)

        val uuid = playerName.minecraftOfflineUuid()

        // Remember unsecure login
        transaction {
            val userLogin = PlayerLogin.findById(uuid) ?: PlayerLogin.new(uuid) {
                name = playerName
                premiumUuid = null
            }

            userLogin.lastLoginAddress = player.address.address.hostAddress
        }
    }

    /**
     * Begin an encrypted connection.
     */
    private fun encryptConnection(packetEvent: PacketEvent, player: Player, name: String, publicKey: PublicKey) {
        logger.info("Initiating secure connection for player $name")

        val verifyToken = MinecraftEncryption.generateVerifyToken()

        try {
            val encryptionBegin = WrapperLoginServerEncryptionBegin().apply {
                writeServerId(serverId)
                writePublicKey(publicKey)
                writeVerifyToken(verifyToken)
            }
            encryptionBegin.sendPacket(player)
        } catch (ex: Exception) {
            logger.warn("Cannot send encrypt connection. Falling back to unsecure login", ex)
            return
        }

        val session = LoginSession(name, serverId, verifyToken)

        // Pending verification
        Session.pending(player.address, name)
        // Cache the session
        Session.cache(player.address, session)

        // Cancel only if the player has a paid account otherwise login as normal offline player
        synchronized(packetEvent.asyncMarker.processingLock) {
            packetEvent.isCancelled = true
        }
    }
}