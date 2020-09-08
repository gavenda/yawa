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
import org.bukkit.event.HandlerList
import work.gavenda.yawa.Config
import work.gavenda.yawa.Plugin
import work.gavenda.yawa.api.*

private var sleepAnimationTaskId = -1
private var sleepTaskId = -1
private val sleepingWorlds = mutableSetOf<World>()
private lateinit var sleepBedListener: SleepBedListener

/**
 * Enable sleep feature.
 */
fun Plugin.enableSleep() {
    if (Config.Sleep.Disabled) return

    // Placeholders
    Placeholder.register(SleepPlaceholderProvider())

    // Event listeners
    sleepBedListener = SleepBedListener(this, sleepingWorlds)

    // Tasks
    sleepTaskId = bukkitAsyncTimerTask(this, 0, 20) {
        server.worlds.asSequence()
            // World is not sleeping
            .filter { it !in sleepingWorlds }
            // And is night time
            .filter { it.isNightTime }
            // And happens on the over world
            .filter { it.environment == World.Environment.NORMAL }
            .forEach(this::checkWorldForSleeping)
    }

    // Register events
    server.pluginManager.registerEvents(sleepBedListener, this)
}

/**
 * Disable sleep feature.
 */
fun Plugin.disableSleep() {
    if (Config.Sleep.Disabled) return

    // Event listeners
    HandlerList.unregisterAll(sleepBedListener)
    // Tasks
    server.scheduler.cancelTask(sleepTaskId)
}

private fun Plugin.checkWorldForSleeping(world: World) {
    // Someone is asleep, and we lack more people.
    if (world.hasBegunSleeping) {
        val message = Placeholder
            .withContext(world)
            .parse(Config.Messages.ActionBarSleeping)

        world.sendActionBarIf(message) {
            Config.Sleep.ActionBar.Enabled
        }
    }
    // Everyone is asleep, and we have enough people
    else if (world.isEveryoneSleeping) {
        val message = Placeholder
            .withContext(world)
            .parse(Config.Messages.ActionBarSleepingDone)

        world.sendActionBarIf(message) {
            Config.Sleep.ActionBar.Enabled
        }

        sleepingWorlds.add(world)

        val sleepingMessage = Placeholder
            .withContext(world)
            .parse(Config.Messages.Sleeping.random())

        // Broadcast everyone sleeping
        world.sendMessageIf(sleepingMessage) {
            Config.Sleep.Chat.Enabled
        }

        sleepAnimationTaskId = bukkitTimerTask(this, 1, 1) {
            val time = world.time
            val dayTime = 1200
            val timeRate = 50
            val timeStart = (dayTime - timeRate * 1.5).toInt()
            val isMorning = time in timeStart..dayTime

            // Time within range, we have reached morning
            if (isMorning) {
                // Remove world from set
                sleepingWorlds.remove(world)

                val sleepingDoneMessage = Placeholder
                    .withContext(world)
                    .parse(Config.Messages.SleepingDone.random())

                // Broadcast successful sleep
                world.sendMessageIf(sleepingDoneMessage) {
                    Config.Sleep.Chat.Enabled
                }
                // Finish
                server.scheduler.cancelTask(sleepAnimationTaskId)
            }
            // Out of range, keep animating
            else {
                world.time = time + timeRate
            }
        }
    }
}
