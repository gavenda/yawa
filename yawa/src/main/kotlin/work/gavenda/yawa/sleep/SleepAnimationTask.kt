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

package work.gavenda.yawa.sleep

import org.bukkit.Statistic
import org.bukkit.World
import work.gavenda.yawa.*
import work.gavenda.yawa.api.compat.sendMessageCompat
import work.gavenda.yawa.api.placeholder.Placeholders
import java.util.*

/**
 * Runs a simulated night-day animation by changing the time per server tick.
 */
class SleepAnimationTask(
    private val world: World,
    private val sleepAnimationTaskIds: MutableMap<UUID, Int>,
    private val sleepingWorlds: MutableSet<UUID>
) : Runnable {

    override fun run() {
        val time = world.time
        val dayTime = 1200
        val timeRate = Config.Sleep.TimeRate
        val timeStart = (dayTime - timeRate * 1.5).toInt()
        val isMorning = time in timeStart..dayTime

        // Time within range, we have reached morning
        if (isMorning) {
            val sleepingDoneMessage = Placeholders
                .withContext(world)
                .parseUsingDefaultLocale(Message.SleepingDone)

            // Broadcast successful sleep
            if (Config.Sleep.Chat.Enabled) {
                world.sendMessageCompat(sleepingDoneMessage)
            }

            // Clear thunder and storm
            world.isThundering = false
            world.setStorm(false)

            // Reset phantom statistics
            world.players.forEach { it.setStatistic(Statistic.TIME_SINCE_REST, 0) }

            // Finish
            sleepAnimationTaskIds[world.uid]?.let { taskId ->
                scheduler.cancelTask(taskId)
                sleepAnimationTaskIds[world.uid] = -1
            }

            // Remove later
            scheduler.runTaskLater(Yawa.Instance, { _ ->
                sleepingWorlds.remove(world.uid)
            }, 20 * 10)
        }
        // Out of range, keep animating
        else {
            world.time = time + timeRate
        }
    }
}