/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 *  Copyright (C) 2021 Gavenda <gavenda@disroot.org>
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
 *
 */

package work.gavenda.yawa.tablist

import work.gavenda.yawa.Config
import work.gavenda.yawa.api.compat.sendPlayerListFooterCompat
import work.gavenda.yawa.api.compat.sendPlayerListHeaderCompat
import work.gavenda.yawa.api.placeholder.Placeholders
import work.gavenda.yawa.server

class TabListTask : Runnable {
    override fun run() {
        val onlinePlayers = server.onlinePlayers

        for (player in onlinePlayers) {
            player.sendPlayerListHeaderCompat(
                Placeholders
                    .withContext(player)
                    .parse(Config.TabList.Header)
            )
            player.sendPlayerListFooterCompat(
                Placeholders
                    .withContext(player)
                    .parse(Config.TabList.Footer)
            )
        }
    }
}