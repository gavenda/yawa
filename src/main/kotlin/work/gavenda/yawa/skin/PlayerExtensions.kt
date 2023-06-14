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

package work.gavenda.yawa.skin

import org.bukkit.entity.Player
import work.gavenda.yawa.Config
import work.gavenda.yawa.api.applySkin
import work.gavenda.yawa.api.compat.schedulerCompat
import work.gavenda.yawa.api.mojang.MOJANG_KEY_TEXTURES
import work.gavenda.yawa.api.mojang.MojangApi
import work.gavenda.yawa.plugin

/**
 * Restore player skin as it is found in Mojang servers.
 */
fun Player.restoreSkin() {
    schedulerCompat.runAtNextTickAsynchronously(plugin) {
        val uuid = if (server.onlineMode) {
            uniqueId
        } else MojangApi.findUuidByName(name)

        if (uuid != null) {
            MojangApi.findProfile(uuid)?.let { playerProfile ->
                playerProfile.properties
                    .find { it.name == MOJANG_KEY_TEXTURES }
                    ?.let { texture -> applySkin(texture.value, texture.signature) }
            }
        } else {
            applySkin(
                Config.Skin.DefaultTexture.Value,
                Config.Skin.DefaultTexture.Signature
            )
        }
    }
}