/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (c) 2023 Gavenda <gavenda@disroot.org>
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
 */

package work.gavenda.yawa.api.compat

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

val Player.displayNameCompat
    get(): Component {
        return pluginEnvironment.displayName(this)
    }

fun Player.kickCompat(component: Component) {
    pluginEnvironment.kickPlayer(this, component)
}

val Player.localeCompat: Locale get() = pluginEnvironment.locale(this)

var Player.playerListNameCompat: Component?
    get() = pluginEnvironment.playerListName(this)
    set(value) = pluginEnvironment.playerListName(this, value)

var Player.playerListHeaderCompat: Component
    get() = pluginEnvironment.playerListHeader(this)
    set(value) = pluginEnvironment.playerListHeader(this, value)

var Player.playerListFooterCompat: Component
    get() = pluginEnvironment.playerListFooter(this)
    set(value) = pluginEnvironment.playerListFooter(this, value)

var PlayerQuitEvent.quitMessageCompat
    get(): Component? {
        return pluginEnvironment.quitMessage(this)
    }
    set(value) = pluginEnvironment.quitMessage(this, value)

var PlayerJoinEvent.joinMessageCompat
    get(): Component? {
        return pluginEnvironment.joinMessage(this)
    }
    set(value) = pluginEnvironment.joinMessage(this, value)

var PlayerDeathEvent.deathMessageCompat
    get(): Component? {
        return pluginEnvironment.deathMessage(this)
    }
    set(value) = pluginEnvironment.deathMessage(this, value)
