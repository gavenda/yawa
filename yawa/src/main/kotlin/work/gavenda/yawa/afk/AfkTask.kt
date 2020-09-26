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

package work.gavenda.yawa.afk

import work.gavenda.yawa.Config
import work.gavenda.yawa.Permission
import work.gavenda.yawa.api.Placeholder
import work.gavenda.yawa.api.isAfk
import work.gavenda.yawa.api.sendMessageIf
import work.gavenda.yawa.api.translateColorCodes
import work.gavenda.yawa.server
import java.util.concurrent.TimeUnit

class AfkTask : Runnable {

    override fun run() {
        server.onlinePlayers
            .filter { it.hasPermission(Permission.AFK) }
            .forEach { player ->
                val afkDelta = System.currentTimeMillis() - player.lastInteractionMillis
                val afkSeconds = TimeUnit.MILLISECONDS.toSeconds(afkDelta)
                val isNotAfk = !player.isAfk

                if (isNotAfk && afkSeconds > Config.Afk.Seconds) {
                    player.isAfk = true

                    val message = Placeholder
                        .withContext(player)
                        .parse(Config.Messages.AfkEntryMessage)
                        .translateColorCodes()
                    val selfMessage = Placeholder
                        .withContext(player)
                        .parse(Config.Messages.PlayerAfkStart)
                        .translateColorCodes()

                    player.world.sendMessageIf(message) {
                        Config.Afk.MessageEnabled
                    }
                    player.sendMessage(selfMessage)
                }

                if (player.isAfk) {
                    player.setPlayerListName(
                        Placeholder.withContext(player)
                            .parse(Config.Afk.PlayerListName)
                            .translateColorCodes()
                    )
                } else {
                    player.setPlayerListName(null)
                }
            }
    }

}