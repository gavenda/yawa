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
import work.gavenda.yawa.*
import work.gavenda.yawa.api.Placeholder
import work.gavenda.yawa.api.sendActionBarIf
import work.gavenda.yawa.api.sendMessageIf
import work.gavenda.yawa.api.translateColorCodes
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Checks per world if there are people beginning to sleep.
 */
class SleepCheckTask(
    private val sleepAnimationTaskIds: MutableMap<UUID, Int>,
    private val sleepingWorlds: MutableSet<UUID>
) : Runnable {

    // Kick seconds should increment per tick (1 second)
    private var kickSeconds = AtomicInteger()

    private fun checkWorld(world: World) {
        val sleepAnimationTaskId = sleepAnimationTaskIds[world.uid] ?: -1
        val sleepRequired = world.players.size / 2

        // Someone is asleep, and we lack more people.
        when {
            world.beganSleeping -> {
                val message = Placeholder
                    .withContext(world)
                    .parseWithDefaultLocale(Message.ActionBarSleeping)
                    .translateColorCodes()

                world.sendActionBarIf(message) {
                    Config.Sleep.ActionBar.Enabled
                }

                // Sleeping @ 50%
                if (world.sleepingPlayers.size >= sleepRequired) {
                    // Less than 15 seconds, increment counter
                    if (kickSeconds.incrementAndGet() < 15) {
                        return
                    }

                    scheduler.runTask(plugin) { _ ->
                        // Kick awake players
                        world.awakePlayers.forEach {
                            val kickMessage = Messages.forPlayer(it)
                                .get(Message.SleepKickMessage)
                                .translateColorCodes()

                            it.sleepKicked = true
                            it.kickPlayer(kickMessage)
                        }
                    }
                }
            }
            // Everyone is asleep, and we have enough people
            world.isEveryoneSleeping -> {
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
                    scheduler.cancelTask(sleepAnimationTaskId)

                val sleepAnimationTask = SleepAnimationTask(world, sleepAnimationTaskIds, sleepingWorlds)

                // Begin sleep animation
                sleepAnimationTaskIds[world.uid] = scheduler.runTaskTimer(plugin, sleepAnimationTask, 1, 1).taskId

                // Reset kick seconds
                kickSeconds.lazySet(0)
            }
            else -> {
                // Reset kick seconds
                kickSeconds.lazySet(0)
            }
        }
    }

    override fun run() {
        server.worlds.asSequence()
            // World is not sleeping
            .filter { it.uid !in sleepingWorlds }
            // And is night time
            .filter { it.isNightTime }
            // And happens on the over world
            .filter { it.environment == World.Environment.NORMAL }
            .forEach(this::checkWorld)
    }
}