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
import work.gavenda.yawa.api.*
import java.util.*
import kotlin.math.ceil

/**
 * Checks per world if there are people beginning to sleep.
 */
class SleepCheckTask(
    private val sleepAnimationTaskIds: MutableMap<UUID, Int>,
    private val sleepingWorlds: MutableSet<UUID>
) : Runnable {

    // Kick seconds should increment per tick (1 second)
    private var kickSeconds = 0

    private fun checkWorld(world: World) {
        val sleepAnimationTaskId = sleepAnimationTaskIds[world.uid] ?: -1
        val sleepRequired = ceil(world.players.size * 0.75).toInt()

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
            }
            // Sleeping @ 75%
            world.sleepingPlayers.size > sleepRequired -> {
                // Less than 30 seconds, increment counter
                if (kickSeconds < 30) {
                    kickSeconds += 1
                    return
                }

                // Kick awake players
                world.awakePlayers.forEach {
                    val kickMessage = Messages.forPlayer(it)
                        .get(Message.SleepKickMessage)
                        .translateColorCodes()
                    val kickMessageBroadcast = Placeholder
                        .withContext(it)
                        .parseWithDefaultLocale(Message.SleepKickMessageBroadcast)
                        .translateColorCodes()

                    it.kickPlayer(kickMessage)
                    world.sendMessage(kickMessageBroadcast)
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
            }
            else -> {
                // Reset kick seconds
                kickSeconds = 0
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