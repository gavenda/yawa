/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (C) 2020 Gavenda <gavenda@disroot.org>
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

package work.gavenda.yawa.afk

import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import work.gavenda.yawa.Config
import work.gavenda.yawa.Plugin
import work.gavenda.yawa.api.Placeholder
import work.gavenda.yawa.api.isAfk
import work.gavenda.yawa.api.sendMessageIf

const val META_AFK_LAST = "AfkLast"

/**
 * The last time the player interacted with anything in-game.
 * @return last interact milliseconds
 */
var Player.lastInteractionMillis: Long
    get() = if (hasMetadata(META_AFK_LAST)) {
        getMetadata(META_AFK_LAST)
            .first { it.owningPlugin == Plugin.Instance }
            .asLong()
    } else System.currentTimeMillis()
    set(value) = setMetadata(META_AFK_LAST, FixedMetadataValue(Plugin.Instance, value))

/**
 * Fires a player interaction.
 */
fun Player.doInteract() {
    if (isAfk) {
        isAfk = false

        val message = Placeholder
            .withContext(this)
            .parse(Config.Messages.AfkLeaveMessage)

        world.sendMessageIf(message) {
            Config.Afk.MessageEnabled
        }
    }

    lastInteractionMillis = System.currentTimeMillis()
}

/**
 * Clears the last interaction.
 */
fun Player.clearLastInteract() {
    removeMetadata(META_AFK_LAST, Plugin.Instance)
}