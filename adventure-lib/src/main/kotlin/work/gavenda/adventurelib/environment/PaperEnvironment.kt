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

package work.gavenda.adventurelib.environment

import net.kyori.adventure.text.Component
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PaperEnvironment : Environment {

    override fun sendMessage(sender: CommandSender, component: Component) {
        sender.sendMessage(component)
    }

    override fun sendMessage(world: World, component: Component) {
        world.sendMessage(component)
    }

    override fun sendActionBar(world: World, component: Component) {
        world.sendActionBar(component)
    }

    override fun setPlayerListHeader(player: Player, component: Component) {
        player.sendPlayerListHeader(component)
    }

    override fun setPlayerListFooter(player: Player, component: Component) {
        player.sendPlayerListFooter(component)
    }

    override fun kickPlayer(player: Player, component: Component) {
        player.kick(component)
    }

    override fun setPlayerListName(player: Player, component: Component?) {
        player.playerListName(component)
    }
}