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

package work.gavenda.yawa.api.compat

import net.kyori.adventure.text.Component
import net.kyori.adventure.sound.Sound
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import java.util.*

interface Environment {
    fun sendMessage(sender: CommandSender, component: Component)
    fun sendMessage(world: World, component: Component)
    fun sendActionBar(world: World, component: Component)
    fun playSound(world: World, sound: Sound)
    fun setPlayerListHeader(player: Player, component: Component)
    fun setPlayerListFooter(player: Player, component: Component)
    fun kickPlayer(player: Player, component: Component)
    fun setPlayerListName(player: Player, component: Component?)
    fun joinMessage(joinEvent: PlayerJoinEvent, component: Component?)
    fun quitMessage(quitEvent: PlayerQuitEvent, component: Component?)
    fun registerNewObjective(scoreboard: Scoreboard, name: String, criteria: String, displayName: Component): Objective
    fun lore(meta: SkullMeta, lore: List<Component>)
    fun lore(meta: ItemMeta, lore: List<Component>)
    fun lore(itemStack: ItemStack, lore: List<Component>)
    fun lore(itemStack: ItemStack): List<Component>?
    fun locale(player: Player): Locale
    fun displayNameCompat(player: Player): Component
    fun displayNameCompat(itemMeta: ItemMeta): Component?
}