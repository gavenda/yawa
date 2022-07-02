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
    val x = double("x")
    val y = double("y")
    val z = double("z")
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
    val x = double("x")
    val y = double("y")
    val z = double("z")
}