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