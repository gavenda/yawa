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

package work.gavenda.yawa.login

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import work.gavenda.yawa.login.PlayerLoginSchema.nullable
import java.util.*

/**
 * Represents a user login.
 */
class PlayerLogin(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<PlayerLogin>(PlayerLoginSchema)

    val premium get() = premiumUuid != null
    var premiumUuid by PlayerLoginSchema.premiumUuid
    var name by PlayerLoginSchema.name
    var lastLoginAddress by PlayerLoginSchema.lastLoginAddress
}

/**
 * Represents a user login schema that is created on the database.
 */
object PlayerLoginSchema : UUIDTable("yawa_player_login", "uuid") {
    val premiumUuid = uuid("premium_uuid").nullable()
    val name = varchar("name", 16)
    val lastLoginAddress = varchar("last_login_address", 40)
}

class PlayerIp(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<PlayerIp>(PlayerIpSchema)

    val premium get() = premiumUuid != null
    var offlineUuid by PlayerIpSchema.offlineUuid
    var premiumUuid by PlayerIpSchema.premiumUuid
    var lastSeen by PlayerIpSchema.lastSeen
    var ipAddress by PlayerIpSchema.ipAddress
}

object PlayerIpSchema : UUIDTable("yawa_player_ip", "uuid") {
    val offlineUuid = uuid("offline_uuid")
    val premiumUuid = uuid("premium_uuid").nullable()
    val lastSeen = timestamp("last_seen")
    val ipAddress = varchar("address", 40)
}