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
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.google.common.util.concurrent.RateLimiter
import org.bukkit.plugin.Plugin
import work.gavenda.yawa.*
import work.gavenda.yawa.api.disconnect
import java.security.KeyPair
import java.util.concurrent.TimeUnit

/**
 * Listens to the client login attempt and determines whether we can encrypt the connection or not.
 * Basically determines if you have paid for minecraft.
 */
@Suppress("UnstableApiUsage")
class LoginListener(
    plugin: Plugin,
    private val keyPair: KeyPair
) : PacketAdapter(
    params()
        .plugin(plugin)
        .types(PacketType.Login.Client.START)
        .optionAsync()
) {

    private val nameRegex = Regex("^[0-z_]+\$")
    private val rateLimiter = RateLimiter.create(200.0, 5, TimeUnit.MINUTES)

    override fun onPacketReceiving(packetEvent: PacketEvent) {
        if (packetEvent.isCancelled) return
        if (rateLimiter.tryAcquire().not()) return

        val packet = packetEvent.packet
        val name = packet.gameProfiles.read(0).name
        val player = packetEvent.player

        // Use mojang name check
        if (Config.Login.StrictNames) {
            // Validate name
            if (name.length < 3) {
                player.disconnect(
                    Messages
                        .forPlayer(player)
                        .get(Message.LoginNameShort)
                )
                yawaLogger.warn("Disconnected player '$name' due to invalid name")
                return
            }
            if (name.length > 16) {
                player.disconnect(
                    Messages
                        .forPlayer(player)
                        .get(Message.LoginNameLong)
                )
                yawaLogger.warn("Disconnected player '$name' due to invalid name")
                return
            }
            if (nameRegex.matches(name).not()) {
                player.disconnect(
                    Messages
                        .forPlayer(player)
                        .get(Message.LoginNameIllegal)
                )
                yawaLogger.warn("Disconnected player '$name' due to invalid name")
                return
            }
        }

        // Remove old data every time on a new login in order to keep the session only for one person
        Session.invalidate(player.address!!)

        // Delay processing
        packetEvent.asyncMarker.incrementProcessingDelay()

        val loginConnectionTask = LoginConnectionTask(
            packetEvent = packetEvent,
            player = player,
            name = name,
            keyPair = keyPair
        )

        scheduler.runTaskAsynchronously(plugin, loginConnectionTask)
    }
}