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

import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import java.util.concurrent.CompletableFuture

fun World.sendMessageCompat(component: Component) {
    pluginEnvironment.sendMessage(this, component)
}

fun World.sendActionBarCompat(component: Component) {
    pluginEnvironment.sendActionBar(this, component)
}

fun World.playSoundCompat(sound: Sound) {
    pluginEnvironment.playSound(this, sound)
}

fun World.getChunkAtAsyncCompat(location: Location): CompletableFuture<Chunk> {
    return pluginEnvironment.getChunkAtAsync(this, location)
}