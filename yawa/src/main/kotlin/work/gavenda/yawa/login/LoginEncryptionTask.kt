/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 *  Copyright (C) 2021 Gavenda <gavenda@disroot.org>
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
 *
 */

package work.gavenda.yawa.login

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.reflect.FuzzyReflection
import com.comphenix.protocol.wrappers.WrappedGameProfile
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.*
import work.gavenda.yawa.api.disconnect
import work.gavenda.yawa.api.mojang.MojangApi
import work.gavenda.yawa.api.networkManager
import work.gavenda.yawa.api.spoofedUuid
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.KeyPair
import java.util.*
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
    private val encryptedVerifyToken: ByteArray,
    private val sharedSecret: ByteArray
) : Runnable {

    override fun run() {
        val loginKey = try {
            MinecraftEncryption.decryptSharedKey(keyPair.private, sharedSecret)
        } catch (ex: GeneralSecurityException) {
            player.disconnect(
                Messages
                    .forPlayer(player)
                    .get(Message.LoginError)
            )
            yawaLogger.error("Cannot decrypt received contents", ex)
            return
        }

        val tokenVerified = validateVerifyToken(session, encryptedVerifyToken)
        val encryptionEnabled = enableEncryption(player, loginKey)

        if (tokenVerified.not()) {
            yawaLogger.warn("Unable to verify token")
            player.disconnect(
                Messages
                    .forPlayer(player)
                    .get(Message.LoginInvalidToken)
            )
            return
        }

        if (encryptionEnabled.not()) {
            yawaLogger.warn("Unable to enable encryption")
            player.disconnect(
                Messages
                    .forPlayer(player)
                    .get(Message.LoginInvalidToken)
            )
            return
        }

        val serverId = MinecraftEncryption.generateServerIdHash(session.serverId, loginKey, keyPair.public)
        val socketAddress = player.address
        try {
            val address = socketAddress!!.address
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

                    userLogin.lastLoginAddress = player.address!!.address.hostAddress
                }

                yawaLogger.info("Connection encrypted for player ${session.name}")

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
                                .get(Message.LoginInvalidSession)
                        )
                    } else {
                        // Not logged in before, tell them to reconnect
                        player.disconnect(
                            Messages
                                .forPlayer(player)
                                .get(Message.LoginInvalidSessionRetry)
                        )
                    }
                }

            }
        } catch (ex: IOException) {
            player.disconnect(
                Messages
                    .forPlayer(player)
                    .get(Message.LoginInvalidToken)
            )
            yawaLogger.error("Cannot connect to session server", ex)
        }

        // This is a fake packet; it shouldn't be send to the server
        synchronized(packetEvent.asyncMarker.processingLock) {
            packetEvent.isCancelled = true
        }

        protocolManager
            .asynchronousManager
            .signalPacketTransmission(packetEvent)
    }


    /**
     * Validate the verify token of the current login session.
     * @param session the current login session
     * @param encryptedVerifyToken the encrypted verified token
     */
    private fun validateVerifyToken(session: LoginSession, encryptedVerifyToken: ByteArray): Boolean {
        try {
            val verifyToken = session.verifyToken
            val decryptedVerifyToken = MinecraftEncryption.decrypt(keyPair.private, encryptedVerifyToken)
            // https://github.com/bergerkiller/CraftSource/blob/master/net.minecraft.server/LoginListener.java#L182
            // Check if the verify token are equal to the server sent one
            if (verifyToken.contentEquals(decryptedVerifyToken)) {
                return true
            }
        } catch (ex: Exception) {
            yawaLogger.error("Cannot decrypt received contents", ex)
        }
        return false
    }

    /**
     * Attempt to enable encryption.
     * @return true if successful, otherwise false
     */
    private fun enableEncryption(player: Player, loginKey: SecretKey): Boolean {
        try {
            val encryptMethod = FuzzyReflection
                .fromObject(player.networkManager)
                .getMethodByParameters("a", Cipher::class.java, Cipher::class.java)

            val decryptCipher = MinecraftEncryption.asCipher(Cipher.DECRYPT_MODE, loginKey)
            val encryptCipher = MinecraftEncryption.asCipher(Cipher.ENCRYPT_MODE, loginKey)

            // Encrypt/decrypt following packets
            encryptMethod.invoke(player.networkManager, decryptCipher, encryptCipher)
        } catch (ex: Exception) {
            yawaLogger.error("Cannot enable encryption", ex)
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
        // uuid is ignored by the packet definition
        val fakeProfile = WrappedGameProfile(UUID.randomUUID(), name)
        // See StartPacketListener for packet information
        val startPacket = PacketContainer(PacketType.Login.Client.START).apply {
            gameProfiles.write(0, fakeProfile)
        }
        try {
            // We don't want to handle our own packets so ignore filters
            protocolManager.recieveClientPacket(player, startPacket, false)
        } catch (ex: Exception) {
            yawaLogger.warn("Failed to fake a new start packet")
            // Cancel the event in order to prevent the server receiving an invalid packet
            player.disconnect(
                Messages
                    .forPlayer(player)
                    .get(Message.LoginInvalidToken)
            )
        }
    }
}