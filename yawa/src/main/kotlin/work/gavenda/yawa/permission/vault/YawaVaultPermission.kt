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

package work.gavenda.yawa.permission.vault

import org.bukkit.Bukkit
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.Yawa
import work.gavenda.yawa.permission.*
import work.gavenda.yawa.server
import java.util.*

class YawaVaultPermission : AbstractVaultPermission() {

    /**
     * Assures execution on async thread or throw an error.
     */
    private fun assureAsync() {
        if (server.isPrimaryThread) throw IllegalStateException("Vault lookup cancelled, request done on primary thread")
    }

    /**
     * Modify group permissions.
     * @param groupId group id
     * @param permissionStr permission string
     * @param value permission value
     */
    private fun modifyGroupPermission(groupId: UUID, permissionStr: String, value: Boolean): Boolean {
        assureAsync()

        val playerIds = mutableListOf<UUID>()
        val result = transaction {
            val foundGroup = Group[groupId]
            val groupPermission = GroupPermission.find {
                (GroupPermissionSchema.group eq groupId) and (GroupPermissionSchema.permission eq permissionStr)
            }.firstOrNull() ?: GroupPermission.new { }

            groupPermission.apply {
                group = foundGroup
                permission = permissionStr
                enabled = value
            }

            foundGroup.players.forEach {
                playerIds.add(it.id.value)
            }

            return@transaction true
        }

        playerIds.forEach {
            // Calculate permissions if exists
            val player = Bukkit.getPlayer(it)
            player?.calculatePermissions()
        }

        return result
    }

    /**
     * Modify user permissions.
     * @param uniqueId unique player id
     * @param permissionStr permission string
     * @param value permission value
     */
    private fun modifyUserPermission(uniqueId: UUID, permissionStr: String, value: Boolean): Boolean {
        assureAsync()

        val result = transaction {
            val playerPermission = PlayerPermission.find {
                (PlayerPermissionSchema.player eq uniqueId) and (PlayerPermissionSchema.permission eq permissionStr)
            }.firstOrNull() ?: PlayerPermission.new { }

            playerPermission.apply {
                playerId = PlayerDb[uniqueId]
                permission = permissionStr
                enabled = value
            }

            return@transaction true
        }

        // Calculate permissions if exists
        val player = Bukkit.getPlayer(uniqueId)
        player?.calculatePermissions()

        return result
    }

    override fun lookupUuid(playerId: String): UUID {
        assureAsync()

        return transaction {
            val playerDb = PlayerDb.find { PlayerSchema.name eq playerId }
                .firstOrNull() ?: throw IllegalArgumentException("Cannot find uuid for player $playerId")
            return@transaction playerDb.id.value
        }
    }

    override fun lookupGroupUuid(groupId: String): UUID {
        assureAsync()

        return transaction {
            val g = Group.find { GroupSchema.name eq groupId }
                .firstOrNull() ?: throw IllegalArgumentException("Cannot find uuid for group $groupId")
            return@transaction g.id.value
        }
    }

    override fun playerHasPermission(playerId: UUID, permission: String): Boolean {
        assureAsync()

        return transaction {
            val playerPermission = PlayerPermission
                .find { (PlayerPermissionSchema.player eq playerId) and (PlayerPermissionSchema.permission eq permission) }
                .firstOrNull() ?: return@transaction false

            return@transaction playerPermission.enabled
        }
    }

    override fun playerAddPermission(playerId: UUID, permission: String) =
        modifyUserPermission(playerId, permission, true)

    override fun playerRemovePermission(playerId: UUID, permission: String) =
        modifyUserPermission(playerId, permission, false)

    override fun playerInGroup(playerId: UUID, groupId: UUID): Boolean {
        assureAsync()

        return transaction {
            GroupPlayer
                .find { (GroupPlayerSchema.player eq playerId) and (GroupPlayerSchema.group eq groupId) }
                .empty()
                .not()
        }
    }

    override fun playerAddGroup(playerId: UUID, groupId: UUID): Boolean {
        assureAsync()

        val result = transaction {
            val existingGroup =
                GroupPlayer.find { (GroupPlayerSchema.player eq playerId) and (GroupPlayerSchema.group eq groupId) }
                    .firstOrNull()

            // Existing group for player does not exist, create
            if (existingGroup == null) {
                GroupPlayer.new {
                    player = PlayerDb[playerId]
                    group = Group[groupId]
                }
            }

            return@transaction when (existingGroup) {
                null -> true
                else -> false
            }
        }

        // Calculate permissions if exists
        val player = Bukkit.getPlayer(playerId)
        player?.calculatePermissions()

        return result
    }

    override fun playerRemoveGroup(playerId: UUID, groupId: UUID): Boolean {
        assureAsync()

        val result = transaction {
            val existingGroup =
                GroupPlayer.find { (GroupPlayerSchema.player eq playerId) and (GroupPlayerSchema.group eq groupId) }
                    .firstOrNull()

            // Existing group for player does not exist, remove
            existingGroup?.delete()

            return@transaction when (existingGroup) {
                null -> false
                else -> true
            }
        }

        // Calculate permissions if exists
        val player = Bukkit.getPlayer(playerId)
        player?.calculatePermissions()

        return result
    }

    override fun findPlayerGroups(playerId: UUID): Array<String> {
        assureAsync()

        return transaction {
            GroupPlayer
                .find { GroupPlayerSchema.player eq playerId }
                .with(GroupPlayer::group)
                .map { it.group.name }
                .toTypedArray()
        }
    }

    override fun findPlayerPrimaryGroup(playerId: UUID): String {
        assureAsync()

        return transaction {
            val groupPlayer = GroupPlayer
                .find { GroupPlayerSchema.player eq playerId }
                .with(GroupPlayer::group)
                .firstOrNull()
                ?: throw IllegalStateException("User has no groups, this should not be the case, db was modified?")

            return@transaction groupPlayer.group.name
        }
    }

    override fun groupHasPermission(groupId: UUID, permission: String): Boolean {
        assureAsync()

        return transaction {
            val groupPermission = GroupPermission
                .find { (GroupPermissionSchema.group eq groupId) and (GroupPermissionSchema.permission eq permission) }
                .firstOrNull() ?: return@transaction false

            return@transaction groupPermission.enabled
        }
    }

    override fun groupAddPermission(groupId: UUID, permission: String) =
        modifyUserPermission(groupId, permission, true)

    override fun groupRemovePermission(groupId: UUID, permission: String) =
        modifyUserPermission(groupId, permission, false)

    override fun getName() = Yawa.Instance.name

    override fun getGroups(): Array<String> {
        assureAsync()

        return transaction {
            Group.all()
                .map { it.name }
                .toTypedArray()
        }
    }
}
