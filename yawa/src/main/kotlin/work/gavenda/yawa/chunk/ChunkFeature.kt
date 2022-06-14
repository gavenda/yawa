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

    override fun enableCommands() {
        plugin.getCommand(Command.CHUNK)?.setExecutor(chunkCommand)
    }

    override fun disableCommands() {
        plugin.getCommand(Command.CHUNK)?.setExecutor(DisabledCommand)
    }

    override fun registerPaperEventListeners() {
        pluginManager.registerEvents(chunkCommand)
    }

    override fun unregisterPaperEventListeners() {
        pluginManager.unregisterEvents(chunkCommand)
    }

    override fun registerPlaceholders() {
        Placeholders.register(chunkPlaceholder)
    }

    override fun unregisterPlaceholders() {
        Placeholders.unregister(chunkPlaceholder)
    }
}