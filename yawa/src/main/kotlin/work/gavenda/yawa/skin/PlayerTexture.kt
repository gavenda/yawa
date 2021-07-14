/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 *  Copyright (C) 2021 Gavenda <gavenda@disroot.org>
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
 *
 */

package work.gavenda.yawa.skin

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import work.gavenda.yawa.api.mojang.MojangProfileProperty
import java.util.*

/**
 * Represents a player texture in the database. Usually represented as a mojang profile property.
 * @see MojangProfileProperty
 */
class PlayerTexture(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<PlayerTexture>(PlayerTextureSchema)

    var texture by PlayerTextureSchema.texture
    var signature by PlayerTextureSchema.signature
}

object PlayerTextureSchema : UUIDTable("yawa_player_texture", "uuid") {
    val texture = text("texture")
    val signature = text("signature")
}