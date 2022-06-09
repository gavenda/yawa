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

class PaperEnvironment : Environment {

    override fun playSound(world: World, sound: Sound) {
        world.playSound(sound)
    }

    override fun displayNameCompat(itemMeta: ItemMeta): Component? {
        return itemMeta.displayName()
    }

    override fun lore(itemStack: ItemStack): List<Component>? {
        return itemStack.lore()?.toList()
    }

    override fun lore(itemStack: ItemStack, lore: List<Component>) {
        itemStack.lore(lore)
    }

    override fun lore(meta: ItemMeta, lore: List<Component>) {
        meta.lore(lore)
    }

    override fun displayNameCompat(player: Player): Component {
        return player.displayName()
    }

    override fun locale(player: Player): Locale {
        return player.locale()
    }

    override fun lore(meta: SkullMeta, lore: List<Component>) {
        meta.lore(lore)
    }

    override fun registerNewObjective(
        scoreboard: Scoreboard,
        name: String,
        criteria: String,
        displayName: Component
    ): Objective {
        return scoreboard.registerNewObjective(name, criteria, displayName)
    }

    override fun quitMessage(quitEvent: PlayerQuitEvent, component: Component?) {
        quitEvent.quitMessage(component)
    }

    override fun joinMessage(joinEvent: PlayerJoinEvent, component: Component?) {
        joinEvent.joinMessage(component)
    }

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