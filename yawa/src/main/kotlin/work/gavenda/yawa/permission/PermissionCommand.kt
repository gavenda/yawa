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

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.*
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.HelpList
import java.util.*

private val permissionCommands = listOf("permission", "yawa:permission")

class PermissionCommand : Command(commands = permissionCommands) {

    override fun execute(sender: CommandSender, args: List<String>) {
        HelpList()
            .command(
                "permission player",
                listOf("<player>", "<permission>", "<enabled:true|false>"),
                "Assign a permission to a player",
                Permission.PERMISSION_PLAYER
            )
            .command(
                "permission group",
                listOf("<group>", "<permission>", "<enabled:true|false>"),
                "Assign a permission to a group",
                Permission.PERMISSION_GROUP
            )
            .generateMessages(sender)
            .forEach(sender::sendMessage)
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return subCommandKeys.toList()
    }
}

class PermissionPlayerCommand : Command(Permission.PERMISSION_PLAYER) {

    override fun execute(sender: CommandSender, args: List<String>) = scheduler.runTaskAsynchronously(plugin) { _ ->
        if (args.size == 3) {
            val nameArg = args[0]
            val permissionArg = args[1]
            val enabledArg = args[2].toBoolean()

            transaction {
                val uniqueId = PlayerDb.find { PlayerSchema.name eq nameArg }
                    .firstOrNull()?.id?.value

                if (uniqueId == null) {
                    sender.sendMessageUsingKey(Message.PermissionPlayerNotFound)
                    return@transaction
                }

                yawaLogger.info("UUID of player $nameArg is $uniqueId")

                val playerDb = PlayerDb.findById(uniqueId)

                if (playerDb == null) {
                    sender.sendMessageUsingKey(Message.PermissionPlayerNotLoggedIn)
                    return@transaction
                }

                val playerPermission = PlayerPermission.find {
                    (PlayerPermissionSchema.player eq uniqueId) and (PlayerPermissionSchema.permission eq permissionArg)
                }.firstOrNull() ?: PlayerPermission.new { }

                playerPermission.apply {
                    playerId = playerDb
                    permission = permissionArg
                    enabled = enabledArg
                }

                // Calculate permissions if exists
                val player = Bukkit.getPlayer(uniqueId)

                player?.calculatePermissions()

                sender.sendMessageUsingKey(Message.PermissionApplied)
            }
        }
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return when (args.size) {
            1 -> Bukkit.getOfflinePlayers()
                .map { it.name!! }
                .toList()
            2 -> Bukkit.getServer().pluginManager.plugins
                .flatMap { it.description.permissions }
                .map { it.name }
            3 -> listOf("true", "false")
            else -> emptyList()
        }
    }
}

class PermissionGroupCommand : Command(Permission.PERMISSION_GROUP) {
    override fun execute(sender: CommandSender, args: List<String>) {
        if (args.size == 3) {
            val groupNameArg = args[0]
            val permissionArg = args[1]
            val enabledArg = args[2].toBoolean()
            val playerIds = mutableListOf<UUID>()

            transaction {
                val foundGroup = Group.find { GroupSchema.name eq groupNameArg }.firstOrNull()

                if (foundGroup == null) {
                    sender.sendMessageUsingKey(Message.PermissionGroupNotFound)
                    return@transaction
                }

                val groupPermission = GroupPermission.find {
                    (GroupPermissionSchema.group eq foundGroup.id) and (GroupPermissionSchema.permission eq permissionArg)
                }.firstOrNull() ?: GroupPermission.new { }

                groupPermission.apply {
                    group = foundGroup
                    permission = permissionArg
                    enabled = enabledArg
                }

                foundGroup.players.forEach {
                    playerIds.add(it.id.value)
                }
            }

            playerIds.forEach {
                // Calculate permissions if exists
                val player = Bukkit.getPlayer(it)
                player?.calculatePermissions()
            }

            sender.sendMessageUsingKey(Message.PermissionApplied)
        }
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return when (args.size) {
            1 -> transaction {
                Group.all().map { it.name }
            }
            2 -> Bukkit.getServer().pluginManager.plugins
                .flatMap { it.description.permissions }
                .map { it.name }
            3 -> listOf("true", "false")
            else -> listOf()
        }
    }

}