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
package work.gavenda.yawa.chunk

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.*
import work.gavenda.yawa.api.placeholder.Placeholders

/**
 * Represents the chunk feature.
 */
object ChunkFeature : PluginFeature {
    override val disabled: Boolean
        get() = Config.Chunk.Disabled

    private val chunkPlaceholder = ChunkPlaceholder()
    private val chunkCommand = ChunkCommand().apply {
        sub(ChunkMarkCommand(), "mark")
        sub(ChunkUnmarkCommand(), "unmark")
    }

    override fun createTables() {
        transaction {
            SchemaUtils.create(WorldChunkSchema)
        }
    }

    override fun onEnable() {
        transaction {
            val dbChunks = WorldChunk.all()

            dbChunks.forEach { dbChunk ->
                server.getWorld(dbChunk.name)?.let { world ->
                    if (dbChunk.marked) {
                        world.loadChunk(dbChunk.x, dbChunk.z)
                        world.setChunkForceLoaded(dbChunk.x, dbChunk.z, true)

                        logger.info("Chunk (${dbChunk.x}, ${dbChunk.z}) is marked to keep running")
                    }
                }
            }
        }
    }

    override fun registerCommands() {
        plugin.getCommand(Commands.CHUNK)?.setExecutor(chunkCommand)
    }

    override fun registerEventListeners() {
        pluginManager.registerEvents(chunkCommand)
    }

    override fun unregisterEventListeners() {
        pluginManager.unregisterEvents(chunkCommand)
    }

    override fun registerPlaceholders() {
        Placeholders.register(chunkPlaceholder)
    }

    override fun unregisterPlaceholders() {
        Placeholders.unregister(chunkPlaceholder)
    }
}