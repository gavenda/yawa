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

import org.bukkit.GameRule
import work.gavenda.yawa.*
import work.gavenda.yawa.api.placeholder.Placeholders
import java.util.*

object SleepFeature : PluginFeature {
    override val disabled get() = Config.Sleep.Disabled

    private var sleepTaskId = -1
    private val sleepPlaceholderProvider = SleepPlaceholderProvider()
    private val sleepAnimationTaskIds = mutableMapOf<UUID, Int>()
    private val sleepingWorlds = mutableSetOf<UUID>()
    private val sleepBedListener = SleepListener(sleepingWorlds)

    override fun onEnable() {
        // Disable sleeping on all worlds
        server.worlds.forEach {
            it.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, 101)
        }
    }

    override fun onDisable() {
        // Reset game rule
        server.worlds.forEach {
            it.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, 100)
        }
    }

    override fun registerTasks() {
        val sleepCheckTask = SleepCheckTask(sleepAnimationTaskIds, sleepingWorlds)

        sleepTaskId = scheduler.runTaskTimerAsynchronously(plugin, sleepCheckTask, 0, 20).taskId
    }

    override fun unregisterTasks() {
        scheduler.cancelTask(sleepTaskId)
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