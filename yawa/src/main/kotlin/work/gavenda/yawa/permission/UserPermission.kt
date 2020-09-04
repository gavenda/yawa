package work.gavenda.yawa.permission

import java.util.*

/**
 * Represents a user permission.
 */
data class UserPermission(
    val uuid: UUID,
    val userUUID: UUID,
    val permission: String,
    val enabled: Boolean
)