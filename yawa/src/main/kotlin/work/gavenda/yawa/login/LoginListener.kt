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
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.google.common.util.concurrent.RateLimiter
import org.bukkit.entity.Player
import work.gavenda.yawa.Plugin
import java.security.KeyPair
import java.util.concurrent.TimeUnit

@Suppress("UnstableApiUsage")
class LoginListener(plugin: Plugin) : PacketAdapter(
    params()
        .plugin(plugin)
        .types(PacketType.Login.Client.START, PacketType.Login.Client.ENCRYPTION_BEGIN)
        .optionAsync()
) {

    private val rateLimiter = RateLimiter.create(200.0, 5, TimeUnit.MINUTES)
    private val keyPair: KeyPair = MinecraftEncryption.generateKeyPair()

    override fun onPacketReceiving(packetEvent: PacketEvent) {
        if (packetEvent.isCancelled) return

        val sender = packetEvent.player
        val packetType = packetEvent.packetType

        if (packetType == PacketType.Login.Client.START) {
            if (!rateLimiter.tryAcquire()) return
            onLogin(packetEvent, sender)
        } else {
            onEncryptionBegin(packetEvent, sender)
        }
    }

    private fun onEncryptionBegin(packetEvent: PacketEvent, sender: Player) {
        val sharedSecret = packetEvent.packet.byteArrays.read(0)
        packetEvent.asyncMarker.incrementProcessingDelay()
        val verifyTask = VerifyResponseTask(packetEvent, sender, sharedSecret.copyOf(), keyPair)
        plugin.server.scheduler.runTaskAsynchronously(plugin, verifyTask)
    }

    private fun onLogin(packetEvent: PacketEvent, player: Player) {
        // remove old data every time on a new login in order to keep the session only for one person
        Session.invalidate(player.address)

        // player.getName() won't work at this state
        val packet = packetEvent.packet
        val username = packet.gameProfiles.read(0).name

        packetEvent.asyncMarker.incrementProcessingDelay()

        val nameCheckTask = NameCheckTask(packetEvent, player, username, keyPair.public)
        plugin.server.scheduler.runTaskAsynchronously(plugin, nameCheckTask)
    }
}