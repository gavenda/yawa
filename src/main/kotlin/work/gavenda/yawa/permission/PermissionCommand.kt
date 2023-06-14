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

package work.gavenda.yawa.permission

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.*
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.HelpList

class PermissionCommand : Command() {
    override val commands = listOf("permission")
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
            .generate(sender)
            .forEach { sender.sendMessage(it) }
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return subCommandKeys.toList()
    }
}

class PermissionPlayerCommand : Command() {
    override val permission = Permission.PERMISSION_PLAYER
    override fun execute(sender: CommandSender, args: List<String>) {
        scheduler.runAtNextTickAsynchronously(plugin) {
            if (args.size == 3) {
                val nameArg = args[0]
                val permissionArg = args[1]
                val enabledArg = args[2].toBoolean()

                try {
                    val uniqueId = PermissionFeature.Vault.lookupUuid(nameArg)

                    if (enabledArg) {
                        PermissionFeature.Vault.playerAddPermission(uniqueId, permissionArg)
                    } else {
                        PermissionFeature.Vault.playerRemovePermission(uniqueId, permissionArg)
                    }

                    sender.sendMessageUsingKey(Message.PermissionApplied)
                } catch (ex: IllegalArgumentException) {
                    sender.sendMessageUsingKey(Message.PermissionPlayerNotFound)
                }
            }
        }
    }

    @Suppress("DEPRECATION")
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

class PermissionGroupCommand : Command() {
    override val permission = Permission.PERMISSION_GROUP
    override fun execute(sender: CommandSender, args: List<String>) {
        scheduler.runAtNextTickAsynchronously(plugin) {
            if (args.size == 3) {
                val groupNameArg = args[0]
                val permissionArg = args[1]
                val enabledArg = args[2].toBoolean()

                try {
                    val uniqueId = PermissionFeature.Vault.lookupGroupUuid(groupNameArg)

                    if (enabledArg) {
                        PermissionFeature.Vault.groupAddPermission(uniqueId, permissionArg)
                    } else {
                        PermissionFeature.Vault.groupRemovePermission(uniqueId, permissionArg)
                    }

                    sender.sendMessageUsingKey(Message.PermissionApplied)
                } catch (ex: IllegalArgumentException) {
                    sender.sendMessageUsingKey(Message.PermissionGroupNotFound)
                }
            }
        }
    }

    @Suppress("DEPRECATION")
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