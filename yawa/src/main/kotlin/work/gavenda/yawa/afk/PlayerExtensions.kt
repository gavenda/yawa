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

package work.gavenda.yawa.afk

import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import work.gavenda.yawa.*
import work.gavenda.yawa.api.compat.sendMessageCompat
import work.gavenda.yawa.api.isAfk
import work.gavenda.yawa.api.placeholder.Placeholders

const val META_PLAYER_AFK_LAST = "AfkLast"

/**
 * The last time the player interacted with anything in-game.
 * @return last interact milliseconds
 */
var Player.lastInteractionMillis: Long
    get() = if (hasMetadata(META_PLAYER_AFK_LAST)) {
        getMetadata(META_PLAYER_AFK_LAST)
            .first { it.owningPlugin == Yawa.Instance }
            .asLong()
    } else System.currentTimeMillis()
    set(value) = setMetadata(META_PLAYER_AFK_LAST, FixedMetadataValue(Yawa.Instance, value))

/**
 * Fires a player interaction.
 */
fun Player.doInteract() {
    if (isAfk) {
        isAfk = false

        val message = Placeholders
            .withContext(this)
            .parseWithLocale(this, Message.AfkLeaveMessage)

        if (Config.Afk.MessageEnabled) {
            world.sendMessageCompat(message)
        }

        sendMessageUsingKey(Message.PlayerAfkEnd)
    }

    lastInteractionMillis = System.currentTimeMillis()
}

/**
 * Clears the last interaction.
 */
fun Player.clearLastInteract() {
    removeMetadata(META_PLAYER_AFK_LAST, Yawa.Instance)
}