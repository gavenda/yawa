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
import work.gavenda.yawa.api.placeholder.Placeholders
import work.gavenda.yawa.server
import java.util.function.Consumer

class TabListTask : Consumer<ScheduledTask> {
    override fun accept(task: ScheduledTask) {
        val onlinePlayers = server.onlinePlayers

        for (player in onlinePlayers) {
            player.playerListName(Placeholders.withContext(player).parse(Config.TabList.PlayerListName))
            player.sendPlayerListHeader(Placeholders.withContext(player).parse(Config.TabList.Header))
            player.sendPlayerListFooter(Placeholders.withContext(player).parse(Config.TabList.Footer))
        }
    }
}