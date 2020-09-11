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

package work.gavenda.yawa.skin

import org.bukkit.event.HandlerList
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.Config
import work.gavenda.yawa.DisabledCommand
import work.gavenda.yawa.Plugin

private val skinListener = SkinListener()

/**
 * Enable skin feature.
 */
fun Plugin.enableSkin() {
    if (Config.Skin.Disabled) {
        getCommand("skin")?.setExecutor(DisabledCommand())
        return
    }

    // Init tables if not created
    transaction {
        SchemaUtils.create(PlayerTextureSchema)
    }

    // Register event listeners
    server.pluginManager.registerEvents(skinListener, this)

    val skinCommand = SkinCommand().apply {
        sub(SkinPlayerCommand(), "player")
        sub(SkinResetCommand(), "reset")
        sub(SkinUrlCommand(), "url")
    }

    getCommand("skin")?.setExecutor(skinCommand)
}

/**
 * Disable skin feature.
 */
fun Plugin.disableSkin() {
    if (Config.Skin.Disabled) return

    getCommand("skin")?.setExecutor(null)

    // Unregister event listeners
    HandlerList.unregisterAll(skinListener)
}