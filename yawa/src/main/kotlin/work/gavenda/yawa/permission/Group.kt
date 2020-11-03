/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (C) 2020 Gavenda <gavenda@disroot.org>
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

package work.gavenda.yawa.permission

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

const val PERMISSION_DEFAULT_GROUP = "yawa:default"

/**
 * Represents a group.
 */
class Group(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<Group>(GroupSchema) {
        val DefaultGroupUuid: UUID = UUID.nameUUIDFromBytes(PERMISSION_DEFAULT_GROUP.toByteArray(Charsets.UTF_8))
    }

    var name by GroupSchema.name
    var players by PlayerDb via GroupPlayerSchema
    val permissions by GroupPermission referrersOn GroupPermissionSchema.group
}

class GroupPlayer(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<GroupPlayer>(GroupPlayerSchema)

    var group by Group referencedOn GroupPlayerSchema.group
    var player by PlayerDb referencedOn GroupPlayerSchema.player
    val groupId by GroupPlayerSchema.group
    val playerId by GroupPlayerSchema.player
}

object GroupSchema : UUIDTable("yawa_group") {
    val name = varchar("name", 50)
}

object GroupPlayerSchema : UUIDTable("yawa_group_player", "uuid") {
    val group = reference("group_uuid", GroupSchema)
    val player = reference("player_uuid", PlayerSchema)
    override val primaryKey = PrimaryKey(group, player)
}