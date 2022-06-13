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

package work.gavenda.yawa.api.placeholder.provider

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import work.gavenda.yawa.api.placeholder.PlaceholderProvider

/**
 * Provides common placeholders for server information.
 */
class ServerPlaceholderProvider : PlaceholderProvider {

    companion object {
        const val SERVER_PLAYER_COUNT = "server-player-count"
        const val SERVER_PLAYER_MAX = "server-player-max"
    }

    override fun provideString(player: Player?, world: World?): Map<String, String?> {
        val server = Bukkit.getServer()

        return mapOf(
            "server-player-count" to server.onlinePlayers.size.toString(),
            "server-player-max" to server.maxPlayers.toString(),
        )
    }
}