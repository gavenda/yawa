package work.gavenda.yawa.chunk

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

/**
 * Represents a user login.
 */
class WorldChunk(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<WorldChunk>(WorldChunkSchema)

    var name by WorldChunkSchema.name
    var x by WorldChunkSchema.x
    var z by WorldChunkSchema.z
    var marked by WorldChunkSchema.marked
}

/**
 * Represents a user login schema that is created on the database.
 */
object WorldChunkSchema : UUIDTable("yawa_world_chunk", "uuid") {
    val name = varchar("name", 16)
    val x = integer("x")
    val z = integer("z")
    val marked = bool("marked")

    init {
        uniqueIndex(name, x, z)
    }
}