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
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.reflect.FuzzyReflection
import com.comphenix.protocol.utility.MinecraftReflection
import com.comphenix.protocol.wrappers.Converters
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerKickEvent
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.*
import work.gavenda.yawa.api.compat.ScheduledTaskCompat
import work.gavenda.yawa.api.disconnect
import work.gavenda.yawa.api.isPaperOrFolia
import work.gavenda.yawa.api.mojang.MojangApi
import work.gavenda.yawa.api.networkManager
import work.gavenda.yawa.api.spoofedUuid
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.KeyPair
import java.time.Instant
import java.util.*
import java.util.function.Consumer
import javax.crypto.Cipher
import javax.crypto.SecretKey

/**
 * Decrypts the shared secret and verifies if it is legitimate.
 */
class LoginEncryptionTask(
    private val packetEvent: PacketEvent,
    private val session: LoginSession,
    private val player: Player,
    private val keyPair: KeyPair,
    private val sharedSecret: ByteArray,
) : Consumer<ScheduledTaskCompat> {

    override fun accept(task: ScheduledTaskCompat) {
        val decryptedKey = try {
            MinecraftEncryption.decryptSharedKey(keyPair.private, sharedSecret)
        } catch (ex: GeneralSecurityException) {
            player.disconnect(
                Messages
                    .forPlayer(player)
                    .get(Message.LoginError),
                PlayerKickEvent.Cause.INVALID_PAYLOAD
            )
            logger.error("Cannot decrypt received contents", ex)
            return
        }

        val encryptionEnabled = enableEncryption(player, decryptedKey)

        if (encryptionEnabled.not()) {
            logger.warn("Unable to enable encryption")
            player.disconnect(
                Messages
                    .forPlayer(player)
                    .get(Message.LoginInvalidToken),
                PlayerKickEvent.Cause.INVALID_PAYLOAD
            )
            return
        }

        val serverId = MinecraftEncryption.generateServerIdHash(session.serverId, decryptedKey, keyPair.public)
        val socketAddress = player.address
        try {
            val address = socketAddress!!.address
            val hostAddress = address.hostAddress
            val profile = MojangApi.hasJoined(session.name, serverId, address)
            val uuid = session.name.minecraftOfflineUuid()

            if (profile != null) {
                if (Config.Login.UsePremiumUuid) {
                    player.spoofedUuid = profile.id
                }

                // Remember encrypted connection
                transaction {
                    val userLogin = PlayerLogin.findById(uuid) ?: PlayerLogin.new(uuid) {
                        name = profile.name
                        premiumUuid = profile.id
                    }

                    userLogin.lastLoginAddress = hostAddress

                    val userIp = PlayerIp.find {
                        (PlayerIpSchema.offlineUuid) eq uuid and (PlayerIpSchema.ipAddress eq hostAddress)
                    }.firstOrNull() ?: PlayerIp.new(UUID.randomUUID()) {
                        offlineUuid = uuid
                        premiumUuid = profile.id
                        ipAddress = hostAddress
                    }

                    userIp.lastSeen = Instant.now()
                }

                logger.info("Connection encrypted for player ${session.name}")

                receiveFakeStartPacket(player, profile.name)
            } else {
                transaction {
                    val userLogin = PlayerLogin.findById(uuid)

                    // Check if user has already logged in before and is a verified premium player
                    if (userLogin != null && userLogin.premium) {
                        // Then invalid session
                        player.disconnect(
                            Messages
                                .forPlayer(player)
                                .get(Message.LoginInvalidSession),
                            PlayerKickEvent.Cause.INVALID_PAYLOAD
                        )
                    } else {
                        // Not logged in before, tell them to reconnect
                        player.disconnect(
                            Messages
                                .forPlayer(player)
                                .get(Message.LoginInvalidSessionRetry),
                            PlayerKickEvent.Cause.INVALID_PAYLOAD
                        )
                    }
                }

            }
        } catch (ex: IOException) {
            player.disconnect(
                Messages
                    .forPlayer(player)
                    .get(Message.LoginInvalidToken),
                PlayerKickEvent.Cause.INVALID_PAYLOAD
            )
            logger.error("Cannot connect to session server", ex)
        }

        // This is a fake packet; it shouldn't be sent to the server
        synchronized(packetEvent.asyncMarker.processingLock) {
            packetEvent.isCancelled = true
        }

        protocolManager
            .asynchronousManager
            .signalPacketTransmission(packetEvent)
    }

    /**
     * Attempt to enable encryption.
     * @return true if successful, otherwise false
     */
    private fun enableEncryption(player: Player, loginKey: SecretKey): Boolean {
        try {
            // Encrypt/decrypt following packets
            if (isPaperOrFolia) {
                logger.info("Paper/Folia detected, using paper encryption")
                val encryptMethod = FuzzyReflection.fromClass(MinecraftReflection.getNetworkManagerClass())
                    .getMethodByParameters("setupEncryption", SecretKey::class.java)

                encryptMethod.invoke(player.networkManager, loginKey)
            } else {
                logger.info("Using default minecraft encryption method")
                val encryptMethod = FuzzyReflection.fromClass(MinecraftReflection.getNetworkManagerClass())
                    .getMethodByParameters("a", Cipher::class.java, Cipher::class.java)
                val decryptCipher = MinecraftEncryption.asCipher(Cipher.DECRYPT_MODE, loginKey)
                val encryptCipher = MinecraftEncryption.asCipher(Cipher.ENCRYPT_MODE, loginKey)

                encryptMethod.invoke(player.networkManager, decryptCipher, encryptCipher)
            }
        } catch (ex: Exception) {
            logger.error("Cannot enable encryption", ex)
            return false
        }
        return true
    }

    /**
     * Fake a new login packet in order to let the server handle all the other stuff.
     * @param player player to be receiving from
     * @param name login name
     */
    private fun receiveFakeStartPacket(player: Player, name: String) {
        val loginSession = Session.find(player.address!!)!!

        val startPacket = PacketContainer(PacketType.Login.Client.START).apply {
            strings.write(0, name)
            getOptionals(Converters.passthrough(UUID::class.java)).write(0, loginSession.uuid)
        }
        try {
            // We don't want to handle our own packets so ignore filters
            protocolManager.receiveClientPacket(player, startPacket, false)
        } catch (ex: Exception) {
            logger.warn("Failed to fake a new start packet")
            // Cancel the event in order to prevent the server receiving an invalid packet
            player.disconnect(
                Messages
                    .forPlayer(player)
                    .get(Message.LoginInvalidToken),
                PlayerKickEvent.Cause.INVALID_PAYLOAD
            )
        }
    }
}