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

package work.gavenda.yawa.tablist

import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import work.gavenda.yawa.Config
import work.gavenda.yawa.PluginFeature
import work.gavenda.yawa.plugin
import work.gavenda.yawa.scheduler

object TabListFeature : PluginFeature {
    override val disabled get() = Config.TabList.Disabled

    private lateinit var tabListTask: ScheduledTask

    override fun registerTasks() {
        tabListTask = scheduler.runAtFixedRate(plugin, TabListTask()::accept, 20, 1)
    }

    override fun unregisterTasks() {
        tabListTask.cancel()
    }
}