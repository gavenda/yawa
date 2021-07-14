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

package work.gavenda.adventurelib

import net.kyori.adventure.text.Component
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import work.gavenda.adventurelib.environment.BukkitEnvironment
import work.gavenda.adventurelib.environment.Environment
import work.gavenda.adventurelib.environment.PaperEnvironment

object AdventureLib {

    private val advEnvironment: Environment = try {
        Class.forName("com.destroystokyo.paper.PaperConfig")
        PaperEnvironment()
    } catch (e: ClassNotFoundException) {
        BukkitEnvironment()
    }

    fun CommandSender.sendMessageCompat(component: Component) {
        advEnvironment.sendMessage(this, component)
    }

    fun World.sendMessageCompat(component: Component) {
        advEnvironment.sendMessage(this, component)
    }

    fun World.sendActionBarCompat(component: Component) {
        advEnvironment.sendActionBar(this, component)
    }

    fun Player.sendPlayerListHeaderCompat(component: Component) {
        advEnvironment.setPlayerListHeader(this, component)
    }

    fun Player.sendPlayerListFooterCompat(component: Component) {
        advEnvironment.setPlayerListFooter(this, component)
    }

    fun Player.kickCompat(component: Component) {
        advEnvironment.kickPlayer(this, component)
    }

    fun Player.playerListNameCompat(component: Component?) {
        advEnvironment.setPlayerListName(this, component)
    }

}