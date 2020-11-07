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

package work.gavenda.yawa.ender

import org.bukkit.entity.Player
import work.gavenda.yawa.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Represents the ender feature.
 */
object EnderFeature : PluginFeature {

    private val teleportingPlayers = ConcurrentLinkedQueue<Player>()
    private val enderListener = EnderListener(teleportingPlayers)

    override val isDisabled get() = Config.Ender.Disabled

    override fun registerEventListeners() {
        pluginManager.registerEvents(enderListener)
    }

    override fun unregisterEventListeners() {
        pluginManager.unregisterEvents(enderListener)
    }
}