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

package work.gavenda.yawa.api

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import java.util.*

class BukkitEnvironment : Environment {

    @Suppress("DEPRECATION")
    override fun locale(player: Player): Locale {
        return Locale(player.locale)
    }

    @Suppress("DEPRECATION")
    override fun lore(meta: SkullMeta, lore: List<Component>) {
        meta.lore = lore.map { it.toLegacyText() }
    }

    private fun Component.toLegacyText(): String {
        return LegacyComponentSerializer.legacySection().serialize(this)
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
        quitEvent.quitMessage = component?.toLegacyText()
    }

    @Suppress("DEPRECATION")
    override fun joinMessage(joinEvent: PlayerJoinEvent, component: Component?) {
        joinEvent.joinMessage = component?.toLegacyText()
    }

    override fun sendMessage(sender: CommandSender, component: Component) {
        sender.sendMessage(component.toLegacyText())
    }

    override fun sendMessage(world: World, component: Component) {
        world.players.forEach {
            it.sendMessage(component.toLegacyText())
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
