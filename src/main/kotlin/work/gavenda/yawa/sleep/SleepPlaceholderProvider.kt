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

package work.gavenda.yawa.sleep

import org.bukkit.World
import org.bukkit.entity.Player
import work.gavenda.yawa.Config
import work.gavenda.yawa.api.placeholder.PlaceholderProvider

/**
 * Providers placeholders for the sleep feature.
 */
class SleepPlaceholderProvider : PlaceholderProvider {

    companion object {
        const val SLEEPING = "world-sleeping"
        const val AWAKE = "world-awake"
        const val SLEEPING_NEEDED = "world-sleeping-needed"
        const val KICK_REMAINING = "sleep-kick-remaining"
        const val KICK_SECONDS = "sleep-kick-seconds"
    }

    override fun provideWorldString(world: World?): Map<String, String?> {
        return mapOf(
            SLEEPING to world?.sleepingPlayers?.size.toString(),
            AWAKE to world?.awakePlayers?.size.toString(),
            SLEEPING_NEEDED to world?.sleepingNeeded.toString(),
            KICK_REMAINING to world?.remainingSeconds.toString(),
            KICK_SECONDS to Config.Sleep.KickSeconds.toString()
        )
    }
}