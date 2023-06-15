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

import net.kyori.adventure.text.Component
import org.bukkit.World
import org.bukkit.entity.Player
import work.gavenda.yawa.api.latencyInMillis
import work.gavenda.yawa.api.placeholder.PlaceholderProvider

/**
 * Provides common placeholders for player instances.
 */
class PlayerPlaceholderProvider : PlaceholderProvider {

    companion object {
        const val LEVEL = "player-level"
        const val DISPLAY_NAME = "player-display-name"
        const val NAME = "player-name"
        const val PING = "player-ping"
    }

    @Suppress("DEPRECATION")
    override fun providePlayer(player: Player?): Map<String, Component?> {
        return mapOf(
            DISPLAY_NAME to player?.displayName()
        )
    }

    override fun providePlayerString(player: Player?): Map<String, String?> {
        return mapOf(
            NAME to player?.name,
            LEVEL to player?.level.toString().padStart(2, '0'),
            PING to player?.latencyInMillis.toString()
        )
    }
}