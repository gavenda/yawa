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

package work.gavenda.yawa.afk

import org.bukkit.event.HandlerList
import work.gavenda.yawa.*

/**
 * Represents the afk feature.
 */
object AfkFeature : PluginFeature {

    private var afkTaskId = -1
    private val afkListener = AfkListener()
    private val afkCommand = AfkCommand()

    override val isDisabled get() = Config.Afk.Disabled

    override fun registerTasks() {
        afkTaskId = scheduler.runTaskTimer(plugin, AfkTask(), 0, 20).taskId
    }

    override fun enableCommands() {
        plugin.getCommand("afk")?.setExecutor(afkCommand)
    }

    override fun disableCommands() {
        plugin.getCommand("afk")?.setExecutor(DisabledCommand)
    }

    override fun registerEventListeners() {
        pluginManager.registerEvents(afkCommand, plugin)
        pluginManager.registerEvents(afkListener, plugin)
    }

    override fun unregisterTasks() {
        scheduler.cancelTask(afkTaskId)
    }

    override fun unregisterEventListeners() {
        HandlerList.unregisterAll(afkListener)
        HandlerList.unregisterAll(afkCommand)
    }
}