package work.gavenda.yawa.skin

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import work.gavenda.yawa.api.mojang.MojangProfileProperty
import java.util.*

/**
 * Represents a player texture in the database. Usually represented as a mojang profile property.
 * @see MojangProfileProperty
 */
class PlayerTexture(uuid: EntityID<UUID>): UUIDEntity(uuid) {
    var texture by PlayerTextureSchema.texture
    var signature by PlayerTextureSchema.signature
}

object PlayerTextureSchema : UUIDTable("yawa_player_texture", "uuid") {
    val texture = text("texture")
    val signature = text("signature")
}