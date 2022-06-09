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

@file:JvmName("EnvironmentKt")

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

private val pluginRuntimeEnvironment: Environment by lazy {
    SpigotEnvironment()
}

val ItemMeta.displayNameCompat
    get(): Component? {
        return pluginRuntimeEnvironment.displayNameCompat(this)
    }

fun ItemStack.loreCompat(): List<Component>? {
    return pluginRuntimeEnvironment.lore(this)
}

fun ItemStack.loreCompat(lore: List<Component>) {
    pluginRuntimeEnvironment.lore(this, lore)
}

fun ItemMeta.loreCompat(lore: List<Component>) {
    pluginRuntimeEnvironment.lore(this, lore)
}

val Player.displayNameCompat
    get(): Component {
        return pluginRuntimeEnvironment.displayNameCompat(this)
    }

fun Player.localeCompat(): Locale {
    return pluginRuntimeEnvironment.locale(this)
}

fun SkullMeta.loreCompat(lore: List<Component>) {
    pluginRuntimeEnvironment.lore(this, lore)
}

fun Scoreboard.registerNewObjectiveCompat(name: String, criteria: String, displayName: Component): Objective {
    return pluginRuntimeEnvironment.registerNewObjective(this, name, criteria, displayName)
}

fun PlayerQuitEvent.quitMessageCompat(component: Component?) {
    pluginRuntimeEnvironment.quitMessage(this, component)
}

fun PlayerJoinEvent.joinMessageCompat(component: Component?) {
    pluginRuntimeEnvironment.joinMessage(this, component)
}

fun CommandSender.sendMessageCompat(component: Component) {
    pluginRuntimeEnvironment.sendMessage(this, component)
}

fun World.sendMessageCompat(component: Component) {
    pluginRuntimeEnvironment.sendMessage(this, component)
}

fun World.sendActionBarCompat(component: Component) {
    pluginRuntimeEnvironment.sendActionBar(this, component)
}

fun World.playSoundCompat(sound: Sound) {
    pluginRuntimeEnvironment.playSound(this, sound)
}

fun Player.sendPlayerListHeaderCompat(component: Component) {
    pluginRuntimeEnvironment.setPlayerListHeader(this, component)
}

fun Player.sendPlayerListFooterCompat(component: Component) {
    pluginRuntimeEnvironment.setPlayerListFooter(this, component)
}

fun Player.kickCompat(component: Component) {
    pluginRuntimeEnvironment.kickPlayer(this, component)
}

fun Player.playerListNameCompat(component: Component?) {
    pluginRuntimeEnvironment.setPlayerListName(this, component)
}