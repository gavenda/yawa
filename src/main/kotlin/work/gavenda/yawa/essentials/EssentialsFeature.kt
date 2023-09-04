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
    private val spawnCommand = SpawnCommand()
    private val backCommand = BackCommand()
    private val warpCommand = WarpCommand()
    private val warpSetCommand = WarpSetCommand()
    private val locationPlaceholder = LocationPlaceholder()
    private val warpDeleteCommand = WarpDeleteCommand()
    private val giveLevelCommand = GiveLevelCommand()

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

    override fun registerCommands() {
        plugin.getCommand(Commands.HOME_TELEPORT)?.setExecutor(homeCommand)
        plugin.getCommand(Commands.HOME_SET)?.setExecutor(homeSetCommand)
        plugin.getCommand(Commands.TELEPORT_SPAWN)?.setExecutor(spawnCommand)
        plugin.getCommand(Commands.TELEPORT_DEATH)?.setExecutor(backCommand)
        plugin.getCommand(Commands.WARP_SET)?.setExecutor(warpSetCommand)
        plugin.getCommand(Commands.WARP_TELEPORT)?.setExecutor(warpCommand)
        plugin.getCommand(Commands.WARP_DELETE)?.setExecutor(warpDeleteCommand)
        plugin.getCommand(Commands.GIVE_LEVEL)?.setExecutor(giveLevelCommand)
    }

    override fun registerEventListeners() {
        pluginManager.registerEvents(homeCommand)
        pluginManager.registerEvents(homeSetCommand)
        pluginManager.registerEvents(spawnCommand)
        pluginManager.registerEvents(backCommand)
        pluginManager.registerEvents(warpSetCommand)
        pluginManager.registerEvents(warpCommand)
        pluginManager.registerEvents(warpDeleteCommand)
        pluginManager.registerEvents(giveLevelCommand)
    }

    override fun unregisterEventListeners() {
        pluginManager.unregisterEvents(giveLevelCommand)
        pluginManager.unregisterEvents(warpDeleteCommand)
        pluginManager.unregisterEvents(warpCommand)
        pluginManager.unregisterEvents(warpSetCommand)
        pluginManager.unregisterEvents(backCommand)
        pluginManager.unregisterEvents(spawnCommand)
        pluginManager.unregisterEvents(homeSetCommand)
        pluginManager.unregisterEvents(homeCommand)
    }
}