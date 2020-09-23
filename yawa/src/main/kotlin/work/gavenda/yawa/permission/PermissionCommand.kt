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
import work.gavenda.yawa.Config
import work.gavenda.yawa.Permission
import work.gavenda.yawa.Plugin
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.HelpList
import work.gavenda.yawa.api.bukkitAsyncTask
import work.gavenda.yawa.api.translateColorCodes
import work.gavenda.yawa.logger
import work.gavenda.yawa.login.PlayerLogin
import work.gavenda.yawa.login.PlayerLoginSchema

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

    override fun execute(sender: CommandSender, args: List<String>) = bukkitAsyncTask(Plugin.Instance) {
        if (args.size == 3) {
            val nameArg = args[0]
            val permissionArg = args[1]
            val enabledArg = args[2].toBoolean()

            transaction {
                // Check if login feature is enabled, then use premium uuid, otherwise, standard bukkit lookup
                val uniqueId = if (Config.Login.Disabled.not() && Config.Login.UsePremiumUuid) {
                    PlayerLogin.find { PlayerLoginSchema.name eq nameArg }
                        .firstOrNull()?.premiumUuid
                } else Bukkit.getPlayerUniqueId(nameArg)

                if (uniqueId == null) {
                    sender.sendMessage(
                        Config.Messages.PermissionPlayerNotFound
                            .translateColorCodes()
                    )
                    return@transaction
                }

                logger.info("UUID of player $nameArg is $uniqueId")

                val playerDb = PlayerDb.findById(uniqueId)

                if (playerDb == null) {
                    sender.sendMessage(
                        Config.Messages.PermissionPlayerNotLoggedIn
                            .translateColorCodes()
                    )
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

                sender.sendMessage(
                    Config.Messages.PermissionApplied
                        .translateColorCodes()
                )
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

            transaction {
                val foundGroup = Group.find { GroupSchema.name eq groupNameArg }.firstOrNull()

                if (foundGroup == null) {
                    sender.sendMessage(
                        Config.Messages.PermissionGroupNotFound
                            .translateColorCodes()
                    )
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
                    // Calculate permissions if exists
                    val player = Bukkit.getPlayer(it.id.value)
                    player?.calculatePermissions()
                }

                sender.sendMessage(
                    Config.Messages.PermissionApplied
                        .translateColorCodes()
                )
            }
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