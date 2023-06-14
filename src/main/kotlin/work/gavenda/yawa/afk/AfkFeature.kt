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

package work.gavenda.yawa.afk

import work.gavenda.yawa.*
import work.gavenda.yawa.api.compat.ScheduledTaskCompat

/**
 * Represents the afk feature.
 */
object AfkFeature : PluginFeature {

    private lateinit var afkTask: ScheduledTaskCompat
    private val afkListener = AfkListener()
    private val afkCommand = AfkCommand()

    override val disabled get() = Config.Afk.Disabled

    override fun registerTasks() {
        afkTask = scheduler.runAtFixedRate(plugin, 1L, 20L, AfkTask()::accept)
    }

    override fun enableCommands() {
        plugin.getCommand(Commands.AFK)?.setExecutor(afkCommand)
    }

    override fun disableCommands() {
        plugin.getCommand(Commands.AFK)?.setExecutor(DisabledCommand)
    }

    override fun registerEventListeners() {
        pluginManager.registerEvents(afkCommand)
        pluginManager.registerEvents(afkListener)
    }


    override fun unregisterTasks() {
        afkTask.cancel()
    }

    override fun unregisterEventListeners() {
        pluginManager.unregisterEvents(afkListener)
        pluginManager.unregisterEvents(afkCommand)
    }
}