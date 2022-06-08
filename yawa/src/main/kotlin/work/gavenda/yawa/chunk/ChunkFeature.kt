package work.gavenda.yawa.chunk

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.*
import work.gavenda.yawa.api.placeholder.Placeholders

/**
 * Represents the chunk feature.
 */
object ChunkFeature : PluginFeature {
    override val isDisabled: Boolean
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
            val chunks = WorldChunk.all()

            chunks.forEach { chunk ->
                server.getWorld(chunk.name)?.let { world ->
                    if (chunk.marked) {
                        world.loadChunk(chunk.x, chunk.z)
                        world.setChunkForceLoaded(chunk.x, chunk.z, true)

                        logger.info("Chunk (${chunk.x}, ${chunk.z}) is marked to keep running")
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