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

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.World
import work.gavenda.yawa.*
import work.gavenda.yawa.api.compat.kickCompat
import work.gavenda.yawa.api.compat.playSoundCompat
import work.gavenda.yawa.api.compat.sendActionBarCompat
import work.gavenda.yawa.api.compat.sendMessageCompat
import work.gavenda.yawa.api.placeholder.Placeholders
import java.util.*
import kotlin.math.ceil

/**
 * Checks per world if there are people beginning to sleep.
 */
class SleepCheckTask(
    private val sleepAnimationTaskIds: MutableMap<UUID, Int>,
    private val sleepingWorlds: MutableSet<UUID>
) : Runnable {

    private fun checkWorld(world: World) {
        val sleepAnimationTaskId = sleepAnimationTaskIds[world.uid] ?: -1
        val sleepRequired = ceil(world.players.size / 2.0)

        // Someone is asleep, and we lack more people.
        when {
            world.beganSleeping -> {
                val message = Placeholders
                    .withContext(world)
                    .parseUsingDefaultLocale(Message.ActionBarSleeping)

                if (Config.Sleep.ActionBar.Enabled) {
                    world.sendActionBarCompat(message)
                }

                // Sleeping @ 50%
                if (world.sleepingPlayers.size >= sleepRequired) {
                    world.kickSeconds = world.kickSeconds + 1

                    if (world.kickSeconds < Config.Sleep.KickSeconds) {
                        // Don't broadcast anything until 5 seconds remain
                        if (world.remainingSeconds > 5) return

                        val kickBroadcastMessage = Placeholders
                            .withContext(world)
                            .parseUsingDefaultLocale(Message.SleepKickRemainingBroadcast)

                        val note = Key.key("block.note_block.xylophone")
                        val sound = Sound.sound(note, Sound.Source.MASTER, 1f, 1f)

                        if (Config.Sleep.Chat.Enabled) {
                            world.sendMessageCompat(kickBroadcastMessage)
                        }
                        world.playSoundCompat(sound)
                        return
                    }

                    scheduler.runTask(plugin) { _ ->
                        // Kick awake players
                        world.awakePlayers.forEach { player ->
                            val kickMessage = Placeholders
                                .withContext(player)
                                .parseUsingDefaultLocale(Message.SleepKickMessage)

                            val kickAlertMessage = Placeholders
                                .withContext(player)
                                .parseUsingDefaultLocale(Message.SleepKickAlert)

                            player.sleepKicked = true
                            player.kickCompat(kickMessage)
                            player.discordAlert(kickAlertMessage)
                        }
                    }
                }
            }
            // Everyone is asleep, and we have enough people
            world.isEveryoneSleeping -> {
                sleepingWorlds.add(world.uid)

                val message = Placeholders
                    .withContext(world)
                    .parseUsingDefaultLocale(Message.ActionBarSleepingDone)

                if (Config.Sleep.ActionBar.Enabled) {
                    world.sendActionBarCompat(message)
                }

                val sleepingMessage = Placeholders
                    .withContext(world)
                    .parseUsingDefaultLocale(Message.Sleeping)

                // Broadcast everyone sleeping
                if (Config.Sleep.Chat.Enabled) {
                    world.sendMessageCompat(sleepingMessage)
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