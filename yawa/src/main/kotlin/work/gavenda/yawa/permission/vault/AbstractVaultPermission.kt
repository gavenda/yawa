/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
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

    override fun has(world: String, player: String, permission: String): Boolean {
        return playerHasPermission(lookupUuid(player), permission)
    }

    override fun has(world: World, player: String, permission: String): Boolean {
        return playerHasPermission(lookupUuid(player), permission)
    }

    override fun has(player: Player, permission: String): Boolean {
        return player.hasPermission(permission)
    }

    override fun playerHas(world: String, player: String, permission: String): Boolean {
        return playerHasPermission(lookupUuid(player), permission)
    }

    override fun playerHas(world: World, player: String, permission: String): Boolean {
        return playerHasPermission(lookupUuid(player), permission)
    }

    override fun playerHas(world: String, player: OfflinePlayer, permission: String): Boolean {
        return playerHasPermission(player.uniqueId, permission)
    }

    override fun playerHas(player: Player, permission: String): Boolean {
        return player.hasPermission(permission)
    }

    override fun playerAdd(world: String, player: String, permission: String): Boolean {
        return playerAddPermission(lookupUuid(player), permission)
    }

    override fun playerAdd(world: World, player: String, permission: String): Boolean {
        return playerAddPermission(lookupUuid(player), permission)
    }

    override fun playerAdd(world: String, player: OfflinePlayer, permission: String): Boolean {
        return playerAddPermission(player.uniqueId, permission)
    }

    override fun playerAdd(player: Player, permission: String): Boolean {
        return playerAddPermission(player.uniqueId, permission)
    }

    override fun playerRemove(world: String, player: String, permission: String): Boolean {
        return playerRemovePermission(lookupUuid(player), permission)
    }

    override fun playerRemove(world: String, player: OfflinePlayer, permission: String): Boolean {
        return playerRemovePermission(player.uniqueId, permission)
    }

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

    override fun playerInGroup(world: String, player: String, group: String): Boolean {
        return playerInGroup(lookupUuid(player), lookupGroupUuid(group))
    }

    override fun playerInGroup(world: World, player: String, group: String): Boolean {
        return playerInGroup(lookupUuid(player), lookupGroupUuid(group))
    }

    override fun playerInGroup(world: String, player: OfflinePlayer, group: String): Boolean {
        return playerInGroup(player.uniqueId, lookupGroupUuid(group))
    }

    override fun playerInGroup(player: Player, group: String): Boolean {
        return playerInGroup(player.uniqueId, lookupGroupUuid(group))
    }

    override fun playerAddGroup(world: String, player: String, group: String): Boolean {
        return playerAddGroup(lookupUuid(player), lookupGroupUuid(group))
    }

    override fun playerAddGroup(world: World, player: String, group: String): Boolean {
        return playerAddGroup(lookupUuid(player), lookupGroupUuid(group))
    }

    override fun playerAddGroup(world: String, player: OfflinePlayer, group: String): Boolean {
        return playerAddGroup(player.uniqueId, lookupGroupUuid(group))
    }

    override fun playerAddGroup(player: Player, group: String): Boolean {
        return playerAddGroup(player.uniqueId, lookupGroupUuid(group))
    }

    override fun playerRemoveGroup(world: String, player: String, group: String): Boolean {
        return playerRemoveGroup(lookupUuid(player), lookupGroupUuid(group))
    }

    override fun playerRemoveGroup(world: World, player: String, group: String): Boolean {
        return playerRemoveGroup(lookupUuid(player), lookupGroupUuid(group))
    }

    override fun playerRemoveGroup(world: String, player: OfflinePlayer, group: String): Boolean {
        return playerRemoveGroup(player.uniqueId, lookupGroupUuid(group))
    }

    override fun playerRemoveGroup(player: Player, group: String): Boolean {
        return playerRemoveGroup(player.uniqueId, lookupGroupUuid(group))
    }

    override fun getPlayerGroups(world: String, player: String): Array<String> {
        return findPlayerGroups(lookupUuid(player))
    }

    override fun getPlayerGroups(world: World, player: String): Array<String> {
        return findPlayerGroups(lookupUuid(player))
    }

    override fun getPlayerGroups(world: String, player: OfflinePlayer): Array<String> {
        return findPlayerGroups(player.uniqueId)
    }

    override fun getPlayerGroups(player: Player): Array<String> {
        return findPlayerGroups(player.uniqueId)
    }

    override fun getPrimaryGroup(world: String, player: String): String {
        return findPlayerPrimaryGroup(lookupUuid(player))
    }

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