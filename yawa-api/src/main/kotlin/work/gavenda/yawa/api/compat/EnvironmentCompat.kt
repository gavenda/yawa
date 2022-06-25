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
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import work.gavenda.yawa.api.apiLogger
import java.util.*
import java.util.concurrent.CompletableFuture

private val pluginRuntimeEnvironment: Environment by lazy {
    if (pluginEnvironment == PluginEnvironment.PAPER) {
        apiLogger.info("Paper detected, using paper as platform for all bukkit calls")
        PaperEnvironment()
    } else {
        apiLogger.info("Spigot detected, using spigot as platform for all bukkit calls")
        SpigotEnvironment()
    }
}

var ItemMeta.displayNameCompat
    get(): Component? {
        return pluginRuntimeEnvironment.displayName(this)
    }
    set(value) = pluginRuntimeEnvironment.displayName(this, value)

var ItemStack.loreCompat: List<Component>?
    get() {
        return pluginRuntimeEnvironment.lore(this)
    }
    set(lore) {
        pluginRuntimeEnvironment.lore(this, lore)
    }

var ItemMeta.loreCompat: List<Component>?
    get() {
        return pluginRuntimeEnvironment.lore(this)
    }
    set(value) = pluginRuntimeEnvironment.lore(this, value)

var PlayerDeathEvent.deathMessageCompat
    get(): Component? {
        return pluginRuntimeEnvironment.deathMessage(this)
    }
    set(value) = pluginRuntimeEnvironment.deathMessage(this, value)

val Player.displayNameCompat
    get(): Component {
        return pluginRuntimeEnvironment.displayName(this)
    }

var PlayerQuitEvent.quitMessageCompat
    get(): Component? {
        return pluginRuntimeEnvironment.quitMessage(this)
    }
    set(value) = pluginRuntimeEnvironment.quitMessage(this, value)

var PlayerJoinEvent.joinMessageCompat
    get(): Component? {
        return pluginRuntimeEnvironment.joinMessage(this)
    }
    set(value) = pluginRuntimeEnvironment.joinMessage(this, value)

val Player.localeCompat: Locale get() = pluginRuntimeEnvironment.locale(this)

var Player.playerListNameCompat: Component?
    get() = pluginRuntimeEnvironment.playerListName(this)
    set(value) = pluginRuntimeEnvironment.playerListName(this, value)

var SkullMeta.loreCompat: List<Component>?
    get() {
        return pluginRuntimeEnvironment.lore(this)
    }
    set(value) = pluginRuntimeEnvironment.lore(this, value)

var Player.playerListHeaderCompat: Component
    get() = pluginRuntimeEnvironment.playerListHeader(this)
    set(value) = pluginRuntimeEnvironment.playerListHeader(this, value)

var Player.playerListFooterCompat: Component
    get() = pluginRuntimeEnvironment.playerListFooter(this)
    set(value) = pluginRuntimeEnvironment.playerListFooter(this, value)

fun Entity.teleportAsyncCompat(
    location: Location,
    cause: PlayerTeleportEvent.TeleportCause
): CompletableFuture<Boolean> {
    return pluginRuntimeEnvironment.teleportAsync(this, location, cause)
}

fun Entity.teleportAsyncCompat(location: Location): CompletableFuture<Boolean> {
    return pluginRuntimeEnvironment.teleportAsync(this, location)
}

fun Scoreboard.registerNewObjectiveCompat(name: String, criteria: String, displayName: Component): Objective {
    return pluginRuntimeEnvironment.registerNewObjective(this, name, criteria, displayName)
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

fun Player.kickCompat(component: Component) {
    pluginRuntimeEnvironment.kickPlayer(this, component)
}
