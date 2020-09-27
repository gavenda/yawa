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

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.*

object SkinFeature : PluginFeature {
    override val isDisabled get() = Config.Skin.Disabled

    private val skinListener = SkinListener()
    private val skinCommand = SkinCommand().apply {
        sub(SkinPlayerCommand(), "player")
        sub(SkinResetCommand(), "reset")
        sub(SkinUrlCommand(), "url")
    }

    override fun createTables() {
        transaction {
            SchemaUtils.create(PlayerTextureSchema)
        }
    }

    override fun enableCommands() {
        plugin.getCommand(Command.SKIN)?.setExecutor(skinCommand)
    }

    override fun disableCommands() {
        plugin.getCommand(Command.SKIN)?.setExecutor(DisabledCommand)
    }

    override fun registerEventListeners() {
        pluginManager.registerEvents(skinListener)
        pluginManager.registerEvents(skinCommand)
    }

    override fun unregisterEventListeners() {
        pluginManager.unregisterEvents(skinCommand)
        pluginManager.unregisterEvents(skinListener)
    }

}