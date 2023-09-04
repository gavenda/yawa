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
package work.gavenda.yawa.hiddenarmor

import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerGameModeChangeEvent
import work.gavenda.yawa.plugin

class GameModeListener : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onGameModeChange(event: PlayerGameModeChangeEvent) {
        if (!HiddenArmorFeature.hasPlayer(event.player)) return
        if (event.newGameMode == GameMode.CREATIVE) {
            HiddenArmorFeature.addIgnoredPlayer(event.player)
            event.player.updateHiddenArmor()
            HiddenArmorFeature.removeIgnoredPlayer(event.player)
        } else {
            val updateTask = fun(_: ScheduledTask) {
                event.player.updateHiddenArmor()
            }
            event.player.scheduler.run(plugin, updateTask, null)
        }
    }
}