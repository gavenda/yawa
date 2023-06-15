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
package work.gavenda.yawa.essentials

import org.bukkit.World
import org.bukkit.entity.Player
import work.gavenda.yawa.api.placeholder.PlaceholderProvider

class LocationPlaceholder : PlaceholderProvider {

    companion object {
        const val X = "location-x"
        const val Y = "location-y"
        const val Z = "location-z"
    }

    override fun providePlayerString(player: Player?): Map<String, String?> {
        val location = player?.location

        return mapOf(
            X to location?.blockX.toString(),
            Y to location?.blockY.toString(),
            Z to location?.blockZ.toString(),
        )
    }
}