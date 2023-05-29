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
import org.bukkit.metadata.FixedMetadataValue
import work.gavenda.yawa.Config
import work.gavenda.yawa.Yawa
import work.gavenda.yawa.plugin
import kotlin.math.max

/**
 * Returns true if the world is currently on night time.
 */
val World.isNightTime get(): Boolean = time > 12950 || time < 23950

/**
 * Returns all sleeping players in this world.
 */
val World.sleepingPlayers
    get(): List<Player> = players.filter { it.isSleeping }

/**
 * Returns awake players in this world.
 */
val World.awakePlayers
    get(): List<Player> = players.filter { it.isSleeping.not() }


const val META_WORLD_KICK_SECONDS = "SleepKickSeconds"

/**
 * Returns elapsed kick seconds, should increment per tick (1 second).
 */
var World.kickSeconds: Int
    get() = if (hasMetadata(META_WORLD_KICK_SECONDS)) {
        getMetadata(META_WORLD_KICK_SECONDS)
            .first { it.owningPlugin == plugin }
            .asInt()
    } else 0
    set(value) = setMetadata(META_WORLD_KICK_SECONDS, FixedMetadataValue(plugin, value))

/**
 * Remaining seconds before awake players get kicked.
 */
val World.remainingSeconds get(): Int = Config.Sleep.KickSeconds - kickSeconds

/**
 * Actual needed players that are sleeping to pass the night.
 */
val World.sleepingNeeded
    get(): Int {
        val neededUnsafe = players.size - sleepingPlayers.size
        return max(0, neededUnsafe)
    }

/**
 * Returns true if any player begins to sleep.
 */
val World.beganSleeping get(): Boolean = sleepingPlayers.isNotEmpty() && sleepingNeeded > 0

/**
 * Returns true if every player is in bed.
 */
val World.isEveryoneSleeping get(): Boolean = sleepingNeeded == 0 && sleepingPlayers.isNotEmpty()