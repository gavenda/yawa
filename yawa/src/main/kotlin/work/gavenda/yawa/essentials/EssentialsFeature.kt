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

package work.gavenda.yawa.essentials

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.*
import work.gavenda.yawa.api.placeholder.Placeholders

object EssentialsFeature : PluginFeature {
    override val disabled get() = Config.Essentials.Disabled

    private val homeCommand = HomeCommand()
    private val setHomeCommand = SetHomeCommand()
    private val teleportSpawnCommand = TeleportSpawnCommand()
    private val backCommand = BackCommand()
    private val locationPlaceholder = LocationPlaceholder()

    override fun createTables() {
        transaction {
            SchemaUtils.create(PlayerHomeSchema)
        }
    }

    override fun registerPlaceholders() {
        Placeholders.register(locationPlaceholder)
    }

    override fun unregisterPlaceholders() {
        Placeholders.unregister(locationPlaceholder)
    }

    override fun enableCommands() {
        plugin.getCommand(Command.HOME_TELEPORT)?.setExecutor(homeCommand)
        plugin.getCommand(Command.HOME_SET)?.setExecutor(setHomeCommand)
        plugin.getCommand(Command.TELEPORT_SPAWN)?.setExecutor(teleportSpawnCommand)
        plugin.getCommand(Command.TELEPORT_DEATH)?.setExecutor(backCommand)
    }

    override fun disableCommands() {
        plugin.getCommand(Command.HOME_TELEPORT)?.setExecutor(DisabledCommand)
        plugin.getCommand(Command.HOME_SET)?.setExecutor(DisabledCommand)
        plugin.getCommand(Command.TELEPORT_SPAWN)?.setExecutor(DisabledCommand)
        plugin.getCommand(Command.TELEPORT_DEATH)?.setExecutor(DisabledCommand)
    }

    override fun registerPaperEventListeners() {
        pluginManager.registerEvents(homeCommand)
        pluginManager.registerEvents(setHomeCommand)
        pluginManager.registerEvents(teleportSpawnCommand)
        pluginManager.registerEvents(backCommand)
    }

    override fun unregisterPaperEventListeners() {
        pluginManager.unregisterEvents(backCommand)
        pluginManager.unregisterEvents(teleportSpawnCommand)
        pluginManager.unregisterEvents(setHomeCommand)
        pluginManager.unregisterEvents(homeCommand)
    }
}