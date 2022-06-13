package work.gavenda.yawa.hiddenarmor

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

/**
 * Represents a player.
 */
class PlayerArmorDb(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<PlayerArmorDb>(PlayerArmorSchema)

    var hidden by PlayerArmorSchema.hidden
}

object PlayerArmorSchema : UUIDTable("yawa_player_armor", "uuid") {
    val hidden = bool("hidden")
}
