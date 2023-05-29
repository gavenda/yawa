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

import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.plugin.Plugin

interface SchedulerCompat {
    fun runAtNextTick(plugin: Plugin, task: (ScheduledTaskCompat) -> Unit): ScheduledTaskCompat {
        return runDelayed(plugin, 1L, task)
    }
    fun runAtNextTickAsynchronously(plugin: Plugin, task: (ScheduledTaskCompat) -> Unit): ScheduledTaskCompat {
        return runDelayedAsynchronously(plugin, 1L, task)
    }
    fun runDelayed(plugin: Plugin, delay: Long, task: (ScheduledTaskCompat) -> Unit): ScheduledTaskCompat
    fun runDelayedAsynchronously(plugin: Plugin, delay: Long, task: (ScheduledTaskCompat) -> Unit): ScheduledTaskCompat
    fun runAtFixedRate(plugin: Plugin, initial: Long, period: Long, task: (ScheduledTaskCompat) -> Unit): ScheduledTaskCompat
    fun runAtFixedRateAsynchronously(plugin: Plugin, initial: Long, period: Long, task: (ScheduledTaskCompat) -> Unit): ScheduledTaskCompat
}

interface ScheduledTaskCompat {
    fun cancel()
}


/**
 * Compatible scheduler for running either Folia or Paper/Spigot/Bukkit.
 */
val Entity.schedulerCompat: SchedulerCompat
    get() = if (PLUGIN_ENVIRONMENT == PluginEnvironment.FOLIA) {
        FoliaEntitySchedulerCompat(scheduler)
    } else {
        BukkitSchedulerCompat(Bukkit.getScheduler())
    }

/**
 * Compatible schedulers for running either Folia or Paper/Spigot/Bukkit.
 */
object CompatSchedulers {
    val globalRegionScheduler: SchedulerCompat
        get() = if (PLUGIN_ENVIRONMENT == PluginEnvironment.FOLIA) {
            FoliaGlobalRegionSchedulerCompat(Bukkit.getGlobalRegionScheduler())
        } else {
            BukkitSchedulerCompat(Bukkit.getScheduler())
        }

    val asyncScheduler: SchedulerCompat
        get() = if (PLUGIN_ENVIRONMENT == PluginEnvironment.FOLIA) {
            FoliaGlobalRegionSchedulerCompat(Bukkit.getGlobalRegionScheduler())
        } else {
            BukkitSchedulerCompat(Bukkit.getScheduler())
        }
}