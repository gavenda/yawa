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

import io.papermc.paper.threadedregions.scheduler.EntityScheduler
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.plugin.Plugin

class FoliaTaskCompat(private val task: ScheduledTask?) : ScheduledTaskCompat {
    override fun cancel() {
        task?.cancel()
    }
}

class FoliaEntitySchedulerCompat(val scheduler: EntityScheduler) : SchedulerCompat {
    override fun runDelayed(plugin: Plugin, delay: Long, task: (ScheduledTaskCompat) -> Unit): ScheduledTaskCompat {
        val returnTask = scheduler.runDelayed(plugin, fun(foliaTask: ScheduledTask) {
            task(FoliaTaskCompat(foliaTask))
        }, null, 1L)
        return FoliaTaskCompat(returnTask)
    }

    override fun runDelayedAsynchronously(
        plugin: Plugin,
        delay: Long,
        task: (ScheduledTaskCompat) -> Unit
    ): ScheduledTaskCompat {
        val returnTask = scheduler.runDelayed(plugin, fun(foliaTask: ScheduledTask) {
            task(FoliaTaskCompat(foliaTask))
        }, null, 1L)
        return FoliaTaskCompat(returnTask)
    }

    override fun runAtFixedRate(
        plugin: Plugin,
        initial: Long,
        period: Long,
        task: (ScheduledTaskCompat) -> Unit
    ): ScheduledTaskCompat {
        val returnTask = scheduler.runAtFixedRate(plugin, fun(foliaTask: ScheduledTask) {
            task(FoliaTaskCompat(foliaTask))
        }, null, initial, period)
        return FoliaTaskCompat(returnTask)
    }

    override fun runAtFixedRateAsynchronously(
        plugin: Plugin,
        initial: Long,
        period: Long,
        task: (ScheduledTaskCompat) -> Unit
    ): ScheduledTaskCompat {
        val returnTask = scheduler.runAtFixedRate(plugin, fun(foliaTask: ScheduledTask) {
            task(FoliaTaskCompat(foliaTask))
        }, null, initial, period)
        return FoliaTaskCompat(returnTask)
    }
}

class FoliaGlobalRegionSchedulerCompat(val scheduler: GlobalRegionScheduler) : SchedulerCompat {

    override fun runDelayed(plugin: Plugin, delay: Long, task: (ScheduledTaskCompat) -> Unit): ScheduledTaskCompat {
        val returnTask = scheduler.runDelayed(plugin, fun(foliaTask: ScheduledTask) {
            task(FoliaTaskCompat(foliaTask))
        }, delay)
        return FoliaTaskCompat(returnTask)
    }

    override fun runDelayedAsynchronously(
        plugin: Plugin,
        delay: Long,
        task: (ScheduledTaskCompat) -> Unit
    ): ScheduledTaskCompat {
        val returnTask = scheduler.runDelayed(plugin, fun(foliaTask: ScheduledTask) {
            task(FoliaTaskCompat(foliaTask))
        }, delay)
        return FoliaTaskCompat(returnTask)
    }

    override fun runAtFixedRate(
        plugin: Plugin,
        initial: Long,
        period: Long,
        task: (ScheduledTaskCompat) -> Unit
    ): ScheduledTaskCompat {
        val returnTask = scheduler.runAtFixedRate(plugin, fun(foliaTask: ScheduledTask) {
            task(FoliaTaskCompat(foliaTask))
        }, initial, period)
        return FoliaTaskCompat(returnTask)
    }

    override fun runAtFixedRateAsynchronously(
        plugin: Plugin,
        initial: Long,
        period: Long,
        task: (ScheduledTaskCompat) -> Unit
    ): ScheduledTaskCompat {
        val returnTask = scheduler.runAtFixedRate(plugin, fun(foliaTask: ScheduledTask) {
            task(FoliaTaskCompat(foliaTask))
        }, initial, period)
        return FoliaTaskCompat(returnTask)
    }
}
