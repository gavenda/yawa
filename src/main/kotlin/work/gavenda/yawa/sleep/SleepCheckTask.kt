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

import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.World
import org.bukkit.event.player.PlayerKickEvent
import work.gavenda.yawa.*
import work.gavenda.yawa.api.placeholder.Placeholders
import java.util.*
import java.util.function.Consumer
import kotlin.math.ceil

/**
 * Checks per world if there are people beginning to sleep.
 */
class SleepCheckTask(
    private val sleepingWorlds: MutableSet<UUID>
) : Consumer<ScheduledTask> {

    private fun checkWorld(world: World) {
        val sleepRequired = ceil(world.awakePlayers.size / 2.0)

        // Someone is asleep, and we lack more people.
        when {
            // Everyone is asleep, and we have enough people
            world.isEveryoneSleeping -> {
                sleepingWorlds.add(world.uid)

                val message = Placeholders
                    .withContext(world)
                    .parseUsingDefaultLocale(Message.ActionBarSleepingDone)

                if (Config.Sleep.ActionBar.Enabled) {
                    world.sendActionBar(message)
                }

                val sleepingMessage = Placeholders
                    .withContext(world)
                    .parseUsingDefaultLocale(Message.Sleeping)

                // Broadcast everyone sleeping
                if (Config.Sleep.Chat.Enabled) {
                    world.sendMessage(sleepingMessage)
                }

                val sleepAnimationTask = SleepAnimationTask(world, sleepingWorlds)

                scheduler.runAtFixedRate(plugin, sleepAnimationTask::run, 1, 1)

                // Reset kick seconds
                world.kickSeconds = 0
            }

            world.beganSleeping -> {
                val message = Placeholders
                    .withContext(world)
                    .parseUsingDefaultLocale(Message.ActionBarSleeping)

                if (Config.Sleep.ActionBar.Enabled) {
                    world.sendActionBar(message)
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
                            world.sendMessage(kickBroadcastMessage)
                        }
                        world.playSound(sound)
                        return
                    }

                    scheduler.run(plugin) {
                        // Kick awake players
                        world.awakePlayers.forEach { player ->
                            val kickMessage = Placeholders
                                .withContext(player)
                                .parseUsingDefaultLocale(Message.SleepKickMessage)

                            val kickAlertMessage = Placeholders
                                .withContext(player)
                                .parseUsingDefaultLocale(Message.SleepKickAlert)

                            player.sleepKicked = true
                            player.kick(kickMessage, PlayerKickEvent.Cause.IDLING)
                            discordAlert(kickAlertMessage)
                        }
                    }
                }
            }

            else -> {
                // Reset kick seconds
                world.kickSeconds = 0
            }
        }
    }

    override fun accept(task: ScheduledTask) {
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