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

import org.bukkit.World
import work.gavenda.yawa.*
import work.gavenda.yawa.api.compat.kickCompat
import work.gavenda.yawa.api.compat.sendMessageCompat
import work.gavenda.yawa.api.placeholder.Placeholder
import work.gavenda.yawa.api.sendMessageIf
import java.util.*

/**
 * Checks per world if there are people beginning to sleep.
 */
class SleepCheckTask(
    private val sleepAnimationTaskIds: MutableMap<UUID, Int>,
    private val sleepingWorlds: MutableSet<UUID>
) : Runnable {

    private fun checkWorld(world: World) {
        val sleepAnimationTaskId = sleepAnimationTaskIds[world.uid] ?: -1
        val sleepRequired = world.players.size / 2

        // Someone is asleep, and we lack more people.
        when {
            world.beganSleeping -> {
                // Sleeping @ 50%
                if (world.sleepingPlayers.size >= sleepRequired) {
                    if (world.kickSeconds < Config.Sleep.KickSeconds) {
                        world.kickSeconds = world.kickSeconds + 1

                        val kickBroadcastMessage = Placeholder
                            .withContext(world)
                            .parseWithDefaultLocale(Message.SleepKickRemainingBroadcast)

                        world.sendMessageCompat(kickBroadcastMessage)
                        return
                    }

                    scheduler.runTask(plugin) { _ ->
                        // Kick awake players
                        world.awakePlayers.forEach {
                            val kickMessage = Placeholder
                                .withContext(it)
                                .parseWithDefaultLocale(Message.SleepKickMessage)

                            it.sleepKicked = true
                            it.kickCompat(kickMessage)
                        }
                    }
                }
            }
            // Everyone is asleep, and we have enough people
            world.isEveryoneSleeping -> {
                sleepingWorlds.add(world.uid)

                val sleepingMessage = Placeholder
                    .withContext(world)
                    .parseWithDefaultLocale(Message.Sleeping)

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
                world.kickSeconds = 0
            }
            else -> {
                // Reset kick seconds
                world.kickSeconds = 0
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