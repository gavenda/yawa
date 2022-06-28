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
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.advancement.Advancement
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
import work.gavenda.yawa.api.displayAdvancement
import java.util.*
import java.util.concurrent.CompletableFuture

private val runtimeEnvironment: Environment by lazy {
    if (PLUGIN_ENVIRONMENT == PluginEnvironment.PAPER) {
        apiLogger.info("Paper detected, using paper as platform for all bukkit calls")
        PaperEnvironment()
    } else {
        apiLogger.info("Spigot detected, using spigot as platform for all bukkit calls")
        SpigotEnvironment()
    }
}

var ItemMeta.displayNameCompat
    get(): Component? {
        return runtimeEnvironment.displayName(this)
    }
    set(value) = runtimeEnvironment.displayName(this, value)

var ItemStack.loreCompat: List<Component>?
    get() {
        return runtimeEnvironment.lore(this)
    }
    set(lore) {
        runtimeEnvironment.lore(this, lore)
    }

var ItemMeta.loreCompat: List<Component>?
    get() {
        return runtimeEnvironment.lore(this)
    }
    set(value) = runtimeEnvironment.lore(this, value)

var PlayerDeathEvent.deathMessageCompat
    get(): Component? {
        return runtimeEnvironment.deathMessage(this)
    }
    set(value) = runtimeEnvironment.deathMessage(this, value)

val Player.displayNameCompat
    get(): Component {
        return runtimeEnvironment.displayName(this)
    }

var PlayerQuitEvent.quitMessageCompat
    get(): Component? {
        return runtimeEnvironment.quitMessage(this)
    }
    set(value) = runtimeEnvironment.quitMessage(this, value)

var PlayerJoinEvent.joinMessageCompat
    get(): Component? {
        return runtimeEnvironment.joinMessage(this)
    }
    set(value) = runtimeEnvironment.joinMessage(this, value)

val Player.localeCompat: Locale get() = runtimeEnvironment.locale(this)

var Player.playerListNameCompat: Component?
    get() = runtimeEnvironment.playerListName(this)
    set(value) = runtimeEnvironment.playerListName(this, value)

var SkullMeta.loreCompat: List<Component>?
    get() {
        return runtimeEnvironment.lore(this)
    }
    set(value) = runtimeEnvironment.lore(this, value)

var Player.playerListHeaderCompat: Component
    get() = runtimeEnvironment.playerListHeader(this)
    set(value) = runtimeEnvironment.playerListHeader(this, value)

var Player.playerListFooterCompat: Component
    get() = runtimeEnvironment.playerListFooter(this)
    set(value) = runtimeEnvironment.playerListFooter(this, value)

/**
 * Returns the display title. Make sure to check [displayAdvancement] before calling.
 */
val Advancement.displayTitle: Component
    get() = runtimeEnvironment.title(this)

fun Entity.teleportAsyncCompat(
    location: Location,
    cause: PlayerTeleportEvent.TeleportCause
): CompletableFuture<Boolean> {
    return runtimeEnvironment.teleportAsync(this, location, cause)
}

fun Entity.teleportAsyncCompat(location: Location): CompletableFuture<Boolean> {
    return runtimeEnvironment.teleportAsync(this, location)
}

fun Scoreboard.registerNewObjectiveCompat(name: String, criteria: String, displayName: Component): Objective {
    return runtimeEnvironment.registerNewObjective(this, name, criteria, displayName)
}

fun CommandSender.sendMessageCompat(component: Component) {
    runtimeEnvironment.sendMessage(this, component)
}

fun World.sendMessageCompat(component: Component) {
    runtimeEnvironment.sendMessage(this, component)
}

fun World.sendActionBarCompat(component: Component) {
    runtimeEnvironment.sendActionBar(this, component)
}

fun World.playSoundCompat(sound: Sound) {
    runtimeEnvironment.playSound(this, sound)
}

fun Player.kickCompat(component: Component) {
    runtimeEnvironment.kickPlayer(this, component)
}

fun World.getChunkAtAsyncCompat(location: Location): CompletableFuture<Chunk> {
    return runtimeEnvironment.getChunkAtAsync(this, location)
}