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

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.*
import work.gavenda.yawa.api.placeholder.Placeholders

object EssentialsFeature : PluginFeature {
    override val disabled get() = Config.Essentials.Disabled

    private val homeCommand = HomeCommand()
    private val homeSetCommand = HomeSetCommand()
    private val teleportSpawnCommand = TeleportSpawnCommand()
    private val backCommand = BackCommand()
    private val warpCommand = WarpCommand()
    private val warpSetCommand = WarpSetCommand()
    private val locationPlaceholder = LocationPlaceholder()
    private val warpDeleteCommand = WarpDeleteCommand()

    override fun createTables() {
        transaction {
            SchemaUtils.create(PlayerHomeSchema)
            SchemaUtils.create(PlayerLocationSchema)
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
        plugin.getCommand(Command.HOME_SET)?.setExecutor(homeSetCommand)
        plugin.getCommand(Command.TELEPORT_SPAWN)?.setExecutor(teleportSpawnCommand)
        plugin.getCommand(Command.TELEPORT_DEATH)?.setExecutor(backCommand)
        plugin.getCommand(Command.WARP_SET)?.setExecutor(warpSetCommand)
        plugin.getCommand(Command.WARP_TELEPORT)?.setExecutor(warpCommand)
        plugin.getCommand(Command.WARP_DELETE)?.setExecutor(warpDeleteCommand)
    }

    override fun disableCommands() {
        plugin.getCommand(Command.HOME_TELEPORT)?.setExecutor(DisabledCommand)
        plugin.getCommand(Command.HOME_SET)?.setExecutor(DisabledCommand)
        plugin.getCommand(Command.TELEPORT_SPAWN)?.setExecutor(DisabledCommand)
        plugin.getCommand(Command.TELEPORT_DEATH)?.setExecutor(DisabledCommand)
        plugin.getCommand(Command.WARP_SET)?.setExecutor(DisabledCommand)
        plugin.getCommand(Command.WARP_TELEPORT)?.setExecutor(DisabledCommand)
        plugin.getCommand(Command.WARP_DELETE)?.setExecutor(DisabledCommand)
    }

    override fun registerPaperEventListeners() {
        pluginManager.registerEvents(homeCommand)
        pluginManager.registerEvents(homeSetCommand)
        pluginManager.registerEvents(teleportSpawnCommand)
        pluginManager.registerEvents(backCommand)
        pluginManager.registerEvents(warpSetCommand)
        pluginManager.registerEvents(warpCommand)
        pluginManager.registerEvents(warpDeleteCommand)
    }

    override fun unregisterPaperEventListeners() {
        pluginManager.unregisterEvents(warpDeleteCommand)
        pluginManager.unregisterEvents(warpCommand)
        pluginManager.unregisterEvents(warpSetCommand)
        pluginManager.unregisterEvents(backCommand)
        pluginManager.unregisterEvents(teleportSpawnCommand)
        pluginManager.unregisterEvents(homeSetCommand)
        pluginManager.unregisterEvents(homeCommand)
    }
}