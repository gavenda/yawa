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

package work.gavenda.yawa.sleep

import org.bukkit.World
import org.bukkit.entity.Player
import kotlin.math.max

/**
 * Returns true if the world is currently on night time.
 */
val World.isNightTime get(): Boolean = time > 12950 || time < 23950

/**
 * Returns all sleeping players in this world.
 */
val World.sleeping
    get(): List<Player> = players.filter { it.isSleeping }

/**
 * Actual needed players that are sleeping to pass the night.
 */
val World.sleepingNeeded
    get(): Int {
        val neededUnsafe = players.size - sleeping.size
        return max(0, neededUnsafe)
    }

/**
 * Returns true if any player begins to sleep.
 */
val World.hasBegunSleeping get() = sleeping.isNotEmpty() && sleepingNeeded > 0

/**
 * Returns true if every player is in bed.
 */
val World.isEveryoneSleeping get() = sleepingNeeded == 0 && sleeping.isNotEmpty()