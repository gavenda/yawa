/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (c) 2022-2023 Gavenda <gavenda@disroot.org>
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

package work.gavenda.yawa.api

import com.comphenix.protocol.utility.MinecraftReflection
import com.comphenix.protocol.wrappers.BukkitConverters
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import org.bukkit.World
import work.gavenda.yawa.plugin

/**
 * Retrieves debug mode status.
 * @return true if world is in debug mode
 */
val World.debugMode: Boolean
    get() {
        val nmsWorldClass: Class<*> = MinecraftReflection.getNmsWorldClass()
        val localDebugWorld = nmsWorldClass.getDeclaredField("A").apply {
            isAccessible = true
        }
        return localDebugWorld.getBoolean(BukkitConverters.getWorldConverter().getGeneric(this))
    }

/**
 * Returns this world as an audience.
 */
fun World.asAudience(): Audience {
    val namespaceKey = key.toString()
    return plugin.adventure.world(Key.key(namespaceKey))
}