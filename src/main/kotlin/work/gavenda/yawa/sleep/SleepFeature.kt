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
import org.bukkit.GameRule
import org.bukkit.World
import work.gavenda.yawa.*
import work.gavenda.yawa.api.placeholder.Placeholders
import java.util.*

object SleepFeature : PluginFeature {
    override val disabled get() = Config.Sleep.Disabled

    private lateinit var sleepTask: ScheduledTask
    private val sleepPlaceholderProvider = SleepPlaceholderProvider()
    private val sleepingWorlds = mutableSetOf<UUID>()
    private val sleepBedListener = SleepListener(sleepingWorlds)

    override fun onEnable() {
        server.worlds
            .filter { it.environment == World.Environment.NORMAL }
            .forEach {
                it.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, 101)
            }
    }

    override fun onDisable() {
        server.worlds
            .filter { it.environment == World.Environment.NORMAL }
            .forEach {
                it.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, 100)
            }
    }

    override fun registerTasks() {
        val sleepCheckTask = SleepCheckTask(sleepingWorlds)

        sleepTask = scheduler.runAtFixedRate(plugin, sleepCheckTask::accept, 20, 20)
    }

    override fun unregisterTasks() {
        sleepTask.cancel()
    }

    override fun registerEventListeners() {
        pluginManager.registerEvents(sleepBedListener)
    }

    override fun unregisterEventListeners() {
        pluginManager.unregisterEvents(sleepBedListener)
    }

    override fun registerPlaceholders() {
        Placeholders.register(sleepPlaceholderProvider)
    }

    override fun unregisterPlaceholders() {
        Placeholders.unregister(sleepPlaceholderProvider)
    }
}