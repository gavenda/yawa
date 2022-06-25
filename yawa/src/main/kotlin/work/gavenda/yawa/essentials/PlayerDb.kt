package work.gavenda.yawa.essentials

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

/**
 * Represents a player home.
 */
class PlayerHomeDb(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<PlayerHomeDb>(PlayerHomeSchema)

    var world by PlayerHomeSchema.world
    var x by PlayerHomeSchema.x
    var y by PlayerHomeSchema.y
    var z by PlayerHomeSchema.z
}

object PlayerHomeSchema : UUIDTable("yawa_player_home", "uuid") {
    val world = uuid("world_uuid")
    val x = integer("x")
    val y = integer("y")
    val z = integer("z")
}
