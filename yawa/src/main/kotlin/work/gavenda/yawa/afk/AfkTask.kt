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

package work.gavenda.yawa.afk

import work.gavenda.yawa.*
import work.gavenda.yawa.api.compat.playerListNameCompat
import work.gavenda.yawa.api.compat.sendMessageCompat
import work.gavenda.yawa.api.isAfk
import work.gavenda.yawa.api.placeholder.Placeholders
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

                    val message = Placeholders
                        .withContext(player)
                        .parseWithLocale(player, Message.AfkEntryMessage)

                    if (Config.Afk.MessageEnabled) {
                        player.world.sendMessageCompat(message)
                    }
                    player.sendMessageUsingKey(Message.PlayerAfkStart)
                }

                if (player.isAfk) {
                    player.playerListNameCompat = Placeholders.withContext(player).parse(Config.Afk.PlayerListNameAfk)
                } else {
                    player.playerListNameCompat = Placeholders.withContext(player).parse(Config.Afk.PlayerListName)
                }
            }
    }

}