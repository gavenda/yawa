package work.gavenda.yawa.permission

import java.util.*

/**
 * Represents a permission group.
 */
data class GroupPermission(
    val uuid: UUID,
    val group: UUID,
    val permission: String,
    val enabled: Boolean
)