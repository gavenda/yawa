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

import org.bukkit.Statistic
import org.bukkit.World
import work.gavenda.yawa.*
import work.gavenda.yawa.api.*
import java.util.*

object SleepFeature : PluginFeature {
    override val isDisabled get() = Config.Sleep.Disabled

    private var sleepTaskId = -1

    private val sleepPlaceholderProvider = SleepPlaceholderProvider()
    private val sleepAnimationTaskIds = mutableMapOf<UUID, Int>()
    private val sleepingWorlds = mutableSetOf<UUID>()
    private val sleepBedListener = SleepBedListener(sleepingWorlds)

    override fun registerTasks() {
        sleepTaskId = bukkitAsyncTimerTask(plugin, 0, 20) {
            server.worlds.asSequence()
                // World is not sleeping
                .filter { it.uid !in sleepingWorlds }
                // And is night time
                .filter { it.isNightTime }
                // And happens on the over world
                .filter { it.environment == World.Environment.NORMAL }
                .forEach(this::checkWorldForSleeping)
        }
    }

    private fun checkWorldForSleeping(world: World) {
        val sleepAnimationTaskId = sleepAnimationTaskIds[world.uid] ?: -1

        // Someone is asleep, and we lack more people.
        if (world.beganSleeping) {
            val message = Placeholder
                .withContext(world)
                .parseWithDefaultLocale(Message.ActionBarSleeping)
                .translateColorCodes()

            world.sendActionBarIf(message) {
                Config.Sleep.ActionBar.Enabled
            }
        }
        // Everyone is asleep, and we have enough people
        else if (world.isEveryoneSleeping) {
            val message = Placeholder
                .withContext(world)
                .parseWithDefaultLocale(Message.ActionBarSleepingDone)
                .translateColorCodes()

            world.sendActionBarIf(message) {
                Config.Sleep.ActionBar.Enabled
            }

            sleepingWorlds.add(world.uid)

            val sleepingMessage = Placeholder
                .withContext(world)
                .parseWithDefaultLocale(Message.Sleeping)
                .translateColorCodes()

            // Broadcast everyone sleeping
            world.sendMessageIf(sleepingMessage) {
                Config.Sleep.Chat.Enabled
            }

            // Cancel existing task if exists
            if (sleepAnimationTaskId > 0)
                server.scheduler.cancelTask(sleepAnimationTaskId)

            sleepAnimationTaskIds[world.uid] = bukkitTimerTask(plugin, 1, 1) {
                val time = world.time
                val dayTime = 1200
                val timeRate = Config.Sleep.TimeRate
                val timeStart = (dayTime - timeRate * 1.5).toInt()
                val isMorning = time in timeStart..dayTime

                // Time within range, we have reached morning
                if (isMorning) {
                    // Remove world from set
                    sleepingWorlds.remove(world.uid)

                    val sleepingDoneMessage = Placeholder
                        .withContext(world)
                        .parseWithDefaultLocale(Message.SleepingDone)
                        .translateColorCodes()

                    // Broadcast successful sleep
                    world.sendMessageIf(sleepingDoneMessage) {
                        Config.Sleep.Chat.Enabled
                    }

                    // Clear thunder and storm
                    world.isThundering = false
                    world.setStorm(false)

                    // Reset phantom statistics
                    world.players.forEach { it.setStatistic(Statistic.TIME_SINCE_REST, 0) }

                    // Finish
                    sleepAnimationTaskIds[world.uid]?.let { taskId ->
                        server.scheduler.cancelTask(taskId)
                        sleepAnimationTaskIds[world.uid] = -1
                    }
                }
                // Out of range, keep animating
                else {
                    world.time = time + timeRate
                }
            }
        }
    }

    override fun unregisterTasks() {
        scheduler.cancelTask(sleepTaskId)
    }

    override fun registerEventListeners() {
        pluginManager.registerEvents(sleepBedListener)
    }

    override fun unregisterEventListeners() {
        pluginManager.unregisterEvents(sleepBedListener)
    }

    override fun registerPlaceholders() {
        Placeholder.register(sleepPlaceholderProvider)
    }

    override fun unregisterPlaceholders() {
        Placeholder.unregister(sleepPlaceholderProvider)
    }
}