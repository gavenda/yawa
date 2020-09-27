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

package work.gavenda.yawa.tablist

import work.gavenda.yawa.*
import work.gavenda.yawa.api.Placeholder
import work.gavenda.yawa.api.bukkitTimerTask
import work.gavenda.yawa.api.translateColorCodes

object TabListFeature : PluginFeature {
    override val isDisabled get() = Config.TabList.Disabled

    private var tabListTaskId = -1

    override fun registerTasks() {
        tabListTaskId = bukkitTimerTask(plugin, 0, 20) {
            val onlinePlayers = server.onlinePlayers

            for (player in onlinePlayers) {
                player.playerListHeader = Placeholder
                    .withContext(player)
                    .parse(Config.TabList.Header)
                    .translateColorCodes()
                player.playerListFooter = Placeholder
                    .withContext(player)
                    .parse(Config.TabList.Footer)
                    .translateColorCodes()
            }
        }
    }

    override fun unregisterTasks() {
        scheduler.cancelTask(tabListTaskId)
    }
}