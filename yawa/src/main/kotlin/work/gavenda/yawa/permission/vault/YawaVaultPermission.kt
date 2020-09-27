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

package work.gavenda.yawa.permission.vault

import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.Yawa
import work.gavenda.yawa.permission.*
import work.gavenda.yawa.server
import java.util.*

class YawaVaultPermission : AbstractVaultPermission() {

    override fun lookupUuid(player: String): UUID {
        if (server.isPrimaryThread) throw IllegalStateException("Lookup cancelled, request done on primary thread")

        return transaction {
            val playerDb = PlayerDb.find { PlayerSchema.name eq player }
                .firstOrNull() ?: throw IllegalArgumentException("Cannot find uuid for player $player")
            return@transaction playerDb.id.value
        }
    }

    override fun lookupGroupUuid(group: String): UUID {
        if (server.isPrimaryThread) throw IllegalStateException("Lookup cancelled, request done on primary thread")

        return transaction {
            val g = Group.find { GroupSchema.name eq group }
                .firstOrNull() ?: throw IllegalArgumentException("Cannot find uuid for group $group")
            return@transaction g.id.value
        }
    }

    override fun userHasPermission(uuid: UUID, permission: String): Boolean {
        if (server.isPrimaryThread) throw IllegalStateException("Lookup cancelled, request done on primary thread")

        return transaction {
            val playerPermission = PlayerPermission
                .find { (PlayerPermissionSchema.player eq uuid) and (PlayerPermissionSchema.permission eq permission) }
                .firstOrNull() ?: return@transaction false

            return@transaction playerPermission.enabled
        }
    }

    override fun userAddPermission(uuid: UUID, permission: String): Boolean {
        throw NotImplementedError("Yawa does not support modifications within Vault")
    }

    override fun userRemovePermission(uuid: UUID, permission: String): Boolean {
        throw NotImplementedError("Yawa does not support modifications within Vault")
    }

    override fun userInGroup(uuid: UUID, group: UUID): Boolean {
        if (server.isPrimaryThread) throw IllegalStateException("Lookup cancelled, request done on primary thread")

        return transaction {
            GroupPlayer
                .find { (GroupPlayerSchema.player eq uuid) and (GroupPlayerSchema.group eq group) }
                .empty()
                .not()
        }
    }

    override fun userAddGroup(uuid: UUID, group: UUID): Boolean {
        throw NotImplementedError("Yawa does not support modifications within Vault")
    }

    override fun userRemoveGroup(uuid: UUID, group: UUID): Boolean {
        throw NotImplementedError("Yawa does not support modifications within Vault")
    }

    override fun userGetGroups(uuid: UUID): Array<String> {
        if (server.isPrimaryThread) throw IllegalStateException("Lookup cancelled, request done on primary thread")

        return transaction {
            GroupPlayer
                .find { GroupPlayerSchema.player eq uuid }
                .with(GroupPlayer::group)
                .map { it.group.name }
                .toTypedArray()
        }
    }

    override fun userGetPrimaryGroup(uuid: UUID): String {
        if (server.isPrimaryThread) throw IllegalStateException("Lookup cancelled, request done on primary thread")

        return transaction {
            val groupPlayer = GroupPlayer
                .find { GroupPlayerSchema.player eq uuid }
                .with(GroupPlayer::group)
                .firstOrNull()
                ?: throw IllegalStateException("User has no groups, this should not be the case, db was modified?")

            return@transaction groupPlayer.group.name
        }
    }

    override fun groupHasPermission(group: UUID, permission: String): Boolean {
        if (server.isPrimaryThread) throw IllegalStateException("Lookup cancelled, request done on primary thread")

        return transaction {
            val groupPermission = GroupPermission
                .find { (GroupPermissionSchema.group eq group) and (GroupPermissionSchema.permission eq permission) }
                .firstOrNull() ?: return@transaction false

            return@transaction groupPermission.enabled
        }
    }

    override fun groupAddPermission(group: UUID, permission: String): Boolean {
        throw NotImplementedError("Yawa does not support modifications within Vault")
    }

    override fun groupRemovePermission(group: UUID, permission: String): Boolean {
        throw NotImplementedError("Yawa does not support modifications within Vault")
    }

    override fun getName() = Yawa.Instance.name

    override fun getGroups(): Array<String> {
        if (server.isPrimaryThread) throw IllegalStateException("Lookup cancelled, request done on primary thread")

        return transaction {
            Group.all()
                .map { it.name }
                .toTypedArray()
        }
    }
}
