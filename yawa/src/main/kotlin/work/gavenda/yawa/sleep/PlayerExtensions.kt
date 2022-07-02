/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (c) 2022 Gavenda <gavenda@disroot.org>
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

package work.gavenda.yawa.sleep

import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import work.gavenda.yawa.Yawa

const val META_PLAYER_SLEEP_KICKED = "SleepKicked"

/**
 * Returns true if the player was kicked due to not sleeping.
 */
var Player.sleepKicked: Boolean
    get() = if (hasMetadata(META_PLAYER_SLEEP_KICKED)) {
        getMetadata(META_PLAYER_SLEEP_KICKED)
            .first { it.owningPlugin == Yawa.Instance }
            .asBoolean()
    } else false
    set(value) = setMetadata(META_PLAYER_SLEEP_KICKED, FixedMetadataValue(Yawa.Instance, value))
