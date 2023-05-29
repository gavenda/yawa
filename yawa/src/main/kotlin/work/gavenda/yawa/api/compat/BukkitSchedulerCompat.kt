/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (c) 2023 Gavenda <gavenda@disroot.org>
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

package work.gavenda.yawa.api.compat

import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import work.gavenda.yawa.api.apiLogger

class BukkitTaskCompat : ScheduledTaskCompat {
    var task: BukkitTask? = null
    override fun cancel() {
        if (task == null) {
            apiLogger.warn("Task is being cancelled while task has not been setup!")
        }
        task?.cancel()
    }

    fun setup(task: BukkitTask): BukkitTaskCompat {
        this.task = task
        return this
    }
}

@Suppress("DEPRECATION")
class BukkitSchedulerCompat(val scheduler: org.bukkit.scheduler.BukkitScheduler) : SchedulerCompat {
    override fun runDelayed(plugin: Plugin, delay: Long, task: (ScheduledTaskCompat) -> Unit): ScheduledTaskCompat {
        val bukkitTaskCompat = BukkitTaskCompat()
        val bukkitTask = object : BukkitRunnable() {
            override fun run() {
                task(bukkitTaskCompat)
            }
        }
        return bukkitTaskCompat.setup(bukkitTask.runTaskLater(plugin, delay))
    }

    override fun runDelayedAsynchronously(
        plugin: Plugin,
        delay: Long,
        task: (ScheduledTaskCompat) -> Unit
    ): ScheduledTaskCompat {
        val bukkitTaskCompat = BukkitTaskCompat()
        val bukkitTask = object : BukkitRunnable() {
            override fun run() {
                task(bukkitTaskCompat)
            }
        }
        return bukkitTaskCompat.setup(bukkitTask.runTaskLaterAsynchronously(plugin, delay))
    }

    override fun runAtFixedRate(
        plugin: Plugin,
        initial: Long,
        period: Long,
        task: (ScheduledTaskCompat) -> Unit
    ): ScheduledTaskCompat {
        val bukkitTaskCompat = BukkitTaskCompat()
        val bukkitTask = object : BukkitRunnable() {
            override fun run() {
                task(bukkitTaskCompat)
            }
        }
        return bukkitTaskCompat.setup(bukkitTask.runTaskTimer(plugin, initial, period))
    }

    override fun runAtFixedRateAsynchronously(
        plugin: Plugin,
        initial: Long,
        period: Long,
        task: (ScheduledTaskCompat) -> Unit
    ): ScheduledTaskCompat {
        val bukkitTaskCompat = BukkitTaskCompat()
        val bukkitTask = object : BukkitRunnable() {
            override fun run() {
                task(bukkitTaskCompat)
            }
        }
        return bukkitTaskCompat.setup(bukkitTask.runTaskTimerAsynchronously(plugin, initial, period))
    }
}