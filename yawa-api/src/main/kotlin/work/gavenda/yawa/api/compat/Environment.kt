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

import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import java.util.*
import java.util.concurrent.CompletableFuture

interface Environment {
    fun sendMessage(sender: CommandSender, component: Component)
    fun sendMessage(world: World, component: Component)
    fun sendActionBar(world: World, component: Component)
    fun playSound(world: World, sound: Sound)
    fun playerListHeader(player: Player, component: Component)
    fun playerListHeader(player: Player): Component
    fun playerListFooter(player: Player, component: Component)
    fun playerListFooter(player: Player): Component
    fun kickPlayer(player: Player, component: Component)
    fun playerListName(player: Player, component: Component?)
    fun playerListName(player: Player): Component?
    fun deathMessage(deathEvent: PlayerDeathEvent): Component?
    fun deathMessage(deathEvent: PlayerDeathEvent, component: Component?)
    fun joinMessage(joinEvent: PlayerJoinEvent, component: Component?)
    fun joinMessage(joinEvent: PlayerJoinEvent): Component?
    fun quitMessage(quitEvent: PlayerQuitEvent, component: Component?)
    fun quitMessage(quitEvent: PlayerQuitEvent): Component?
    fun registerNewObjective(scoreboard: Scoreboard, name: String, criteria: String, displayName: Component): Objective
    fun lore(meta: SkullMeta, lore: List<Component>)
    fun lore(meta: ItemMeta, lore: List<Component>?)
    fun lore(meta: ItemMeta): List<Component>?
    fun lore(itemStack: ItemStack, lore: List<Component>?)
    fun lore(itemStack: ItemStack): List<Component>?
    fun locale(player: Player): Locale
    fun displayName(player: Player): Component
    fun displayName(itemMeta: ItemMeta): Component?
    fun displayName(itemMeta: ItemMeta, component: Component?)
    fun teleportAsync(entity: Entity, location: Location): CompletableFuture<Boolean>
    fun teleportAsync(entity: Entity, location: Location, cause: TeleportCause): CompletableFuture<Boolean>
}