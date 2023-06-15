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

package work.gavenda.yawa.api.placeholder.provider

import org.bukkit.World
import org.bukkit.entity.Player
import work.gavenda.yawa.api.placeholder.PlaceholderProvider
import work.gavenda.yawa.server

/**
 * Provides common placeholders for server information.
 */
class ServerPlaceholderProvider : PlaceholderProvider {

    companion object {
        const val SERVER_PLAYER_COUNT = "server-player-count"
        const val SERVER_PLAYER_MAX = "server-player-max"
    }

    override fun provideWorldString(world: World?): Map<String, String?> {
        return mapOf(
            SERVER_PLAYER_COUNT to server.onlinePlayers.size.toString(),
            SERVER_PLAYER_MAX to server.maxPlayers.toString(),
        )
    }
}