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

package work.gavenda.yawa.login

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

/**
 * Represents a user login.
 */
class UserLogin(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<UserLogin>(UserLoginSchema)

    val premium get() = premiumUuid != null
    var premiumUuid by UserLoginSchema.premiumUuid
    var name by UserLoginSchema.name
    var lastLoginAddress by UserLoginSchema.lastLoginAddress
}

/**
 * Represents a user login schema that is created on the database.
 */
object UserLoginSchema : UUIDTable("yawa_user_login", "uuid") {
    val premiumUuid = uuid("premium_uuid").nullable()
    val name = varchar("name", 16)
    val lastLoginAddress = varchar("last_login_address", 15)
}