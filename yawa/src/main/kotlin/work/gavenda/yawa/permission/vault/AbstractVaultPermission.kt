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

import net.milkbowl.vault.permission.Permission
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.bukkit.entity.Player
import work.gavenda.yawa.Yawa
import java.util.*

/**
 * An extended abstraction of the Vault [Permission] API.
 *
 * The original Vault API only contained methods to query data by username. Over
 * time, the maintainers added additional methods to query by (Offline)Player, but
 * in order to keep backwards compatibility with implementations which only supported
 * usernames, they implemented the Player query methods and downgraded the requests
 * to get a result using the players username.
 *
 * Whilst this meant the old plugins would still be supported, it made the whole
 * API a total mess. This class reverses this action, and instead upgrades
 * requests to use UUIDs. This makes implementing Vault significantly easier for modern
 * plugins, and because requests are upgraded instead of being downgraded then upgraded,
 * much faster for plugins querying data.
 */
abstract class AbstractVaultPermission : Permission() {

    override fun isEnabled(): Boolean {
        return true
    }

    override fun hasSuperPermsCompat(): Boolean {
        return true
    }

    override fun hasGroupSupport(): Boolean {
        return true
    }

    // methods subclasses are expected to implement
    abstract fun lookupUuid(playerId: String): UUID
    abstract fun lookupGroupUuid(groupId: String): UUID
    abstract fun playerHasPermission(playerId: UUID, permission: String): Boolean
    abstract fun playerAddPermission(playerId: UUID, permission: String): Boolean
    abstract fun playerRemovePermission(playerId: UUID, permission: String): Boolean
    abstract fun playerInGroup(playerId: UUID, groupId: UUID): Boolean
    abstract fun playerAddGroup(playerId: UUID, groupId: UUID): Boolean
    abstract fun playerRemoveGroup(playerId: UUID, groupId: UUID): Boolean
    abstract fun findPlayerGroups(playerId: UUID): Array<String>
    abstract fun findPlayerPrimaryGroup(playerId: UUID): String
    abstract fun groupHasPermission(groupId: UUID, permission: String): Boolean
    abstract fun groupAddPermission(groupId: UUID, permission: String): Boolean
    abstract fun groupRemovePermission(groupId: UUID, permission: String): Boolean

    @Deprecated("Deprecated in Java", ReplaceWith("playerHasPermission(lookupUuid(player), permission)"))
    override fun has(world: String, player: String, permission: String): Boolean {
        return playerHasPermission(lookupUuid(player), permission)
    }

    @Deprecated("Deprecated in Java", ReplaceWith("playerHasPermission(lookupUuid(player), permission)"))
    override fun has(world: World, player: String, permission: String): Boolean {
        return playerHasPermission(lookupUuid(player), permission)
    }

    override fun has(player: Player, permission: String): Boolean {
        return player.hasPermission(permission)
    }

    @Deprecated("Deprecated in Java", ReplaceWith("playerHasPermission(lookupUuid(player), permission)"))
    override fun playerHas(world: String, player: String, permission: String): Boolean {
        return playerHasPermission(lookupUuid(player), permission)
    }

    @Deprecated("Deprecated in Java", ReplaceWith("playerHasPermission(lookupUuid(player), permission)"))
    override fun playerHas(world: World, player: String, permission: String): Boolean {
        return playerHasPermission(lookupUuid(player), permission)
    }

    override fun playerHas(world: String, player: OfflinePlayer, permission: String): Boolean {
        return playerHasPermission(player.uniqueId, permission)
    }

    override fun playerHas(player: Player, permission: String): Boolean {
        return player.hasPermission(permission)
    }

    @Deprecated("Deprecated in Java", ReplaceWith("playerAddPermission(lookupUuid(player), permission)"))
    override fun playerAdd(world: String, player: String, permission: String): Boolean {
        return playerAddPermission(lookupUuid(player), permission)
    }

    @Deprecated("Deprecated in Java", ReplaceWith("playerAddPermission(lookupUuid(player), permission)"))
    override fun playerAdd(world: World, player: String, permission: String): Boolean {
        return playerAddPermission(lookupUuid(player), permission)
    }

    override fun playerAdd(world: String, player: OfflinePlayer, permission: String): Boolean {
        return playerAddPermission(player.uniqueId, permission)
    }

    override fun playerAdd(player: Player, permission: String): Boolean {
        return playerAddPermission(player.uniqueId, permission)
    }

    @Deprecated("Deprecated in Java", ReplaceWith("playerRemovePermission(lookupUuid(player), permission)"))
    override fun playerRemove(world: String, player: String, permission: String): Boolean {
        return playerRemovePermission(lookupUuid(player), permission)
    }

    override fun playerRemove(world: String, player: OfflinePlayer, permission: String): Boolean {
        return playerRemovePermission(player.uniqueId, permission)
    }

    @Deprecated("Deprecated in Java", ReplaceWith("playerRemovePermission(lookupUuid(player), permission)"))
    override fun playerRemove(world: World, player: String, permission: String): Boolean {
        return playerRemovePermission(lookupUuid(player), permission)
    }

    override fun playerRemove(player: Player, permission: String): Boolean {
        return playerRemovePermission(player.uniqueId, permission)
    }

    override fun groupHas(world: String, group: String, permission: String): Boolean {
        return groupHasPermission(lookupGroupUuid(group), permission)
    }

    override fun groupHas(world: World, group: String, permission: String): Boolean {
        return groupHasPermission(lookupGroupUuid(group), permission)
    }

    override fun groupAdd(world: String, group: String, permission: String): Boolean {
        return groupAddPermission(lookupGroupUuid(group), permission)
    }

    override fun groupAdd(world: World, group: String, permission: String): Boolean {
        return groupAddPermission(lookupGroupUuid(group), permission)
    }

    override fun groupRemove(world: String, group: String, permission: String): Boolean {
        return groupRemovePermission(lookupGroupUuid(group), permission)
    }

    override fun groupRemove(world: World, group: String, permission: String): Boolean {
        return groupRemovePermission(lookupGroupUuid(group), permission)
    }

    @Deprecated("Deprecated in Java", ReplaceWith("playerInGroup(lookupUuid(player), lookupGroupUuid(group))"))
    override fun playerInGroup(world: String, player: String, group: String): Boolean {
        return playerInGroup(lookupUuid(player), lookupGroupUuid(group))
    }

    @Deprecated("Deprecated in Java", ReplaceWith("playerInGroup(lookupUuid(player), lookupGroupUuid(group))"))
    override fun playerInGroup(world: World, player: String, group: String): Boolean {
        return playerInGroup(lookupUuid(player), lookupGroupUuid(group))
    }

    override fun playerInGroup(world: String, player: OfflinePlayer, group: String): Boolean {
        return playerInGroup(player.uniqueId, lookupGroupUuid(group))
    }

    override fun playerInGroup(player: Player, group: String): Boolean {
        return playerInGroup(player.uniqueId, lookupGroupUuid(group))
    }

    @Deprecated("Deprecated in Java", ReplaceWith("playerAddGroup(lookupUuid(player), lookupGroupUuid(group))"))
    override fun playerAddGroup(world: String, player: String, group: String): Boolean {
        return playerAddGroup(lookupUuid(player), lookupGroupUuid(group))
    }

    @Deprecated("Deprecated in Java", ReplaceWith("playerAddGroup(lookupUuid(player), lookupGroupUuid(group))"))
    override fun playerAddGroup(world: World, player: String, group: String): Boolean {
        return playerAddGroup(lookupUuid(player), lookupGroupUuid(group))
    }

    override fun playerAddGroup(world: String, player: OfflinePlayer, group: String): Boolean {
        return playerAddGroup(player.uniqueId, lookupGroupUuid(group))
    }

    override fun playerAddGroup(player: Player, group: String): Boolean {
        return playerAddGroup(player.uniqueId, lookupGroupUuid(group))
    }

    @Deprecated("Deprecated in Java", ReplaceWith("playerRemoveGroup(lookupUuid(player), lookupGroupUuid(group))"))
    override fun playerRemoveGroup(world: String, player: String, group: String): Boolean {
        return playerRemoveGroup(lookupUuid(player), lookupGroupUuid(group))
    }

    @Deprecated("Deprecated in Java", ReplaceWith("playerRemoveGroup(lookupUuid(player), lookupGroupUuid(group))"))
    override fun playerRemoveGroup(world: World, player: String, group: String): Boolean {
        return playerRemoveGroup(lookupUuid(player), lookupGroupUuid(group))
    }

    override fun playerRemoveGroup(world: String, player: OfflinePlayer, group: String): Boolean {
        return playerRemoveGroup(player.uniqueId, lookupGroupUuid(group))
    }

    override fun playerRemoveGroup(player: Player, group: String): Boolean {
        return playerRemoveGroup(player.uniqueId, lookupGroupUuid(group))
    }

    @Deprecated("Deprecated in Java", ReplaceWith("findPlayerGroups(lookupUuid(player))"))
    override fun getPlayerGroups(world: String, player: String): Array<String> {
        return findPlayerGroups(lookupUuid(player))
    }

    @Deprecated("Deprecated in Java", ReplaceWith("findPlayerGroups(lookupUuid(player))"))
    override fun getPlayerGroups(world: World, player: String): Array<String> {
        return findPlayerGroups(lookupUuid(player))
    }

    override fun getPlayerGroups(world: String, player: OfflinePlayer): Array<String> {
        return findPlayerGroups(player.uniqueId)
    }

    override fun getPlayerGroups(player: Player): Array<String> {
        return findPlayerGroups(player.uniqueId)
    }

    @Deprecated("Deprecated in Java", ReplaceWith("findPlayerPrimaryGroup(lookupUuid(player))"))
    override fun getPrimaryGroup(world: String, player: String): String {
        return findPlayerPrimaryGroup(lookupUuid(player))
    }

    @Deprecated("Deprecated in Java", ReplaceWith("findPlayerPrimaryGroup(lookupUuid(player))"))
    override fun getPrimaryGroup(world: World, player: String): String {
        return findPlayerPrimaryGroup(lookupUuid(player))
    }

    override fun getPrimaryGroup(world: String, player: OfflinePlayer): String {
        return findPlayerPrimaryGroup(player.uniqueId)
    }

    override fun getPrimaryGroup(player: Player): String {
        return findPlayerPrimaryGroup(player.uniqueId)
    }

    init {
        super.plugin = Yawa.Instance
    }
}