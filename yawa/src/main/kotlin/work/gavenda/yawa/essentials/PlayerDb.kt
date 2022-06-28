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

class PlayerLocationDb(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<PlayerLocationDb>(PlayerLocationSchema)

    var playerUuid by PlayerLocationSchema.playerUuid
    var name by PlayerLocationSchema.name
    var world by PlayerLocationSchema.world
    var x by PlayerLocationSchema.x
    var y by PlayerLocationSchema.y
    var z by PlayerLocationSchema.z
}

object PlayerLocationSchema : UUIDTable("yawa_player_location", "uuid") {
    val playerUuid = uuid("player_uuid")
    val name = varchar("name", 64)
    val world = uuid("world_uuid")
    val x = integer("x")
    val y = integer("y")
    val z = integer("z")
}