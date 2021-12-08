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
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
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
import work.gavenda.yawa.api.toBaseComponent
import work.gavenda.yawa.api.toComponent
import work.gavenda.yawa.api.toLegacyText
import java.util.*

class SpigotEnvironment : Environment {

    @Suppress("DEPRECATION")
    override fun displayNameCompat(itemMeta: ItemMeta): Component {
        return itemMeta.displayName.toComponent()
    }

    @Suppress("DEPRECATION")
    override fun lore(itemStack: ItemStack): List<Component>? {
        return itemStack.itemMeta.lore?.map { it.toComponent() }
    }

    @Suppress("DEPRECATION")
    override fun lore(itemStack: ItemStack, lore: List<Component>) {
        val meta = itemStack.itemMeta.apply {
            setLore(lore.map { it.toLegacyText() })
        }
        itemStack.itemMeta = meta
    }

    @Suppress("DEPRECATION")
    override fun lore(meta: ItemMeta, lore: List<Component>) {
        meta.loreComponents = lore.map { it.toBaseComponent() }
    }

    @Suppress("DEPRECATION")
    override fun displayNameCompat(player: Player): Component {
        return player.displayName.toComponent()
    }

    @Suppress("DEPRECATION")
    override fun locale(player: Player): Locale {
        return Locale(player.locale)
    }

    @Suppress("DEPRECATION")
    override fun lore(meta: SkullMeta, lore: List<Component>) {
        meta.loreComponents = lore.map { it.toBaseComponent() }
    }


    @Suppress("DEPRECATION")
    override fun registerNewObjective(
        scoreboard: Scoreboard,
        name: String,
        criteria: String,
        displayName: Component
    ): Objective {
        return scoreboard.registerNewObjective(name, criteria, displayName.toLegacyText())
    }

    @Suppress("DEPRECATION")
    override fun quitMessage(quitEvent: PlayerQuitEvent, component: Component?) {
        quitEvent.quitMessage = ""
        if (component != null) {
            quitEvent.player.spigot().sendMessage(*component.toBaseComponent())
        }
    }

    @Suppress("DEPRECATION")
    override fun joinMessage(joinEvent: PlayerJoinEvent, component: Component?) {
        joinEvent.joinMessage = ""
        if (component != null) {
            joinEvent.player.spigot().sendMessage(*component.toBaseComponent())
        }
    }

    @Suppress("DEPRECATION")
    override fun sendMessage(sender: CommandSender, component: Component) {
        sender.spigot().sendMessage(*component.toBaseComponent())
    }

    @Suppress("DEPRECATION")
    override fun sendMessage(world: World, component: Component) {
        world.players.forEach {
            it.spigot().sendMessage(*component.toBaseComponent())
        }
    }

    @Suppress("DEPRECATION")
    override fun sendActionBar(world: World, component: Component) {
        val legacyComponent = TextComponent.fromLegacyText(component.toLegacyText())
        world.players.forEach { player ->
            player
                .spigot()
                .sendMessage(ChatMessageType.ACTION_BAR, *legacyComponent)
        }
    }

    @Suppress("DEPRECATION")
    override fun setPlayerListHeader(player: Player, component: Component) {
        player.playerListHeader = component.toLegacyText()
    }

    @Suppress("DEPRECATION")
    override fun setPlayerListFooter(player: Player, component: Component) {
        player.playerListFooter = component.toLegacyText()
    }

    @Suppress("DEPRECATION")
    override fun kickPlayer(player: Player, component: Component) {
        player.kickPlayer(component.toLegacyText())
    }

    @Suppress("DEPRECATION")
    override fun setPlayerListName(player: Player, component: Component?) {
        player.setPlayerListName(component?.toLegacyText())
    }
}
