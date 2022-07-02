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