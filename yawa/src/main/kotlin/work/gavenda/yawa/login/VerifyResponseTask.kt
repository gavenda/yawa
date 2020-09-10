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

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.injector.server.TemporaryPlayerFactory
import com.comphenix.protocol.reflect.FieldUtils
import com.comphenix.protocol.reflect.FuzzyReflection
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedGameProfile
import org.bukkit.entity.Player
import work.gavenda.yawa.api.mojang.MojangApi
import work.gavenda.yawa.api.wrapper.WrapperLoginServerDisconnect
import work.gavenda.yawa.logger
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.KeyPair
import java.util.*
import javax.crypto.SecretKey

class VerifyResponseTask(
    private val packetEvent: PacketEvent,
    private val player: Player,
    private val sharedSecret: ByteArray,
    private val serverKey: KeyPair
) : Runnable {

    override fun run() {
        try {
            val session = Session.find(player.address)
            if (session == null) {
                kickPlayer("invalid-request")
                logger.warn("Attempted to send encryption response at an invalid state")
            } else {
                verifyResponse(session)
            }
        } finally {
            // this is a fake packet; it shouldn't be send to the server
            synchronized(packetEvent.asyncMarker.processingLock) {
                packetEvent.isCancelled = true
            }

            ProtocolLibrary.getProtocolManager()
                .asynchronousManager
                .signalPacketTransmission(packetEvent)
        }
    }

    private fun verifyResponse(session: LoginSession) {
        val privateKey = serverKey.private
        val loginKey = try {
            MinecraftEncryption.decryptSharedKey(privateKey, sharedSecret)
        } catch (securityEx: GeneralSecurityException) {
            kickPlayer("error-kick")
            logger.error("Cannot decrypt received contents", securityEx)
            return
        }
        try {
            if (!checkVerifyToken(session) || !enableEncryption(loginKey)) {
                return
            }
        } catch (ex: Exception) {
            kickPlayer("error-kick")
            logger.error("Cannot decrypt received contents", ex)
            return
        }
        val serverId = MinecraftEncryption.generateServerIdHash("", loginKey, serverKey.public)
        val socketAddress = player.address
        try {
            val address = socketAddress.address
            val profile = MojangApi.hasJoined(session.name, serverId, address)
            if (profile != null) {
                receiveFakeStartPacket(profile.name)
            } else {
                kickPlayer("invalid-session")
            }
        } catch (ex: IOException) {
            kickPlayer("error-kick")
            logger.error("Cannot connect to session server", ex)
        }
    }

    private fun checkVerifyToken(session: LoginSession): Boolean {
        val requestVerify: ByteArray = session.verifyToken
        // Encrypted verify token
        val responseVerify = packetEvent.packet.byteArrays.read(1)

        // https://github.com/bergerkiller/CraftSource/blob/master/net.minecraft.server/LoginListener.java#L182
        if (!requestVerify.contentEquals(MinecraftEncryption.decrypt(serverKey.private, responseVerify))) {
            // check if the verify token are equal to the server sent one
            kickPlayer("invalid-verify-token")
            logger.warn("Attempted to login with an invalid verify token")
            return false
        }
        return true
    }

    private val networkManager: Any
        get() {
            val injectorContainer = TemporaryPlayerFactory.getInjectorFromPlayer(player)
            val injectorClass = Class.forName("com.comphenix.protocol.injector.netty.Injector")
            val rawInjector = FuzzyReflection.getFieldValue(injectorContainer, injectorClass, true)
            return FieldUtils.readField(rawInjector, "networkManager", true)
        }

    private fun enableEncryption(loginKey: SecretKey): Boolean {
        try {
            val encryptMethod = FuzzyReflection
                .fromObject(networkManager)
                .getMethodByParameters("a", SecretKey::class.java)

            // encrypt/decrypt following packets

            // the client expects this behaviour
            encryptMethod.invoke(networkManager, loginKey)
        } catch (ex: Exception) {
            kickPlayer("error-kick")
            logger.error("Cannot enable encryption", ex)
            return false
        }
        return true
    }

    private fun kickPlayer(reason: String = "") {
        val kickPacket = WrapperLoginServerDisconnect().apply {
            writeReason(WrappedChatComponent.fromText(reason))
        }
        // send kick packet at login state
        kickPacket.sendPacket(player)
        // tell the server that we want to close the connection
        player.kickPlayer("Disconnect")
    }

    // fake a new login packet in order to let the server handle all the other stuff
    private fun receiveFakeStartPacket(username: String) {
        // uuid is ignored by the packet definition
        val fakeProfile = WrappedGameProfile(UUID.randomUUID(), username)
        // see StartPacketListener for packet information
        val startPacket = PacketContainer(PacketType.Login.Client.START).apply {
            gameProfiles.write(0, fakeProfile)
        }
        try {
            // We don't want to handle our own packets so ignore filters
            ProtocolLibrary.getProtocolManager().recieveClientPacket(player, startPacket, false)
        } catch (ex: Exception) {
            logger.warn("Failed to fake a new start packet")
            // cancel the event in order to prevent the server receiving an invalid packet
            kickPlayer("error-kick")
        }
    }
}
