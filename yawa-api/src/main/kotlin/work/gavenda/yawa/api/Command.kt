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

package work.gavenda.yawa.api

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import work.gavenda.yawa.api.compat.sendMessageCompat

/**
 * Represents a command executor.
 */
abstract class Command : TabExecutor, Listener {
    private val subCommands = mutableMapOf<String, Command>()

    companion object {
        const val NO_PERMISSION_TEXT = "You do not have enough permissions to use this command"
        val NO_PERMISSION = Component.text(NO_PERMISSION_TEXT, NamedTextColor.RED)
    }

    /**
     * Permission needed to execute the command.
     */
    open val permission: String = ""

    /**
     * List of commands or aliases for this command, needed for async tab completion
     */
    open val commands = listOf<String>()

    val subCommandKeys
        get() = subCommands.keys.toSet()

    fun sub(command: Command, arg: String, vararg aliases: String): Command {
        subCommands[arg] = command
        for (a in aliases) {
            subCommands[a] = command
        }
        return this
    }

    override fun onCommand(
        sender: CommandSender,
        cmd: org.bukkit.command.Command,
        label: String,
        args: Array<String>
    ): Boolean {
        executeSubCommands(sender, this, args.toList())
        return true
    }

    private fun executeSubCommands(
        sender: CommandSender,
        parent: Command,
        args: List<String>
    ) {
        for (arg in args) {
            val cmd = parent.subCommands[arg]
            if (cmd != null) {
                executeSubCommands(sender, cmd, args.subList(1, args.size))
                return
            }
        }
        if (!hasPermission(sender, parent)) {
            sender.sendMessageCompat(NO_PERMISSION)
            return
        }
        parent.execute(sender, args)
    }

    override fun onTabComplete(
        sender: CommandSender,
        cmd: org.bukkit.command.Command,
        label: String,
        args: Array<String>
    ): List<String>? {
        return if (!hasPermission(sender, this)) null else tooltips(sender, this, args.toList())
    }

    @EventHandler(ignoreCancelled = true)
    fun onAsyncTabComplete(e: AsyncTabCompleteEvent) {
        if (!e.isCommand) return

        var buffer = e.buffer
        if (buffer.isEmpty()) return

        if (buffer[0] == '/') {
            buffer = buffer.substring(1)
        }

        val firstSpace = buffer.indexOf(' ')
        if (firstSpace < 0) return

        val sender = e.sender
        val commandArg = buffer.substring(0, firstSpace)

        if (commands.contains(commandArg).not()) return

        val args = buffer.split(' ').drop(1)

        e.completions = if (!hasPermission(sender, this)) {
            emptyList()
        } else tooltips(sender, this, args)
        e.isHandled = true
    }

    private fun tooltips(sender: CommandSender, command: Command, args: List<String>): List<String> {
        for (arg in args) {
            val cmd = command.subCommands[arg]
            if (cmd != null) {
                return tooltips(sender, cmd, args.subList(1, args.size))
            }
        }
        if (hasPermission(sender, command)) {
            if (args.isNotEmpty()) {
                val keys = command.subCommandKeys
                if (keys.isNotEmpty()) {
                    val result: MutableList<String> = ArrayList()
                    val lastArg = args[args.size - 1]
                    for (str in keys) {
                        if (str.startsWith(lastArg)) result.add(str)
                    }
                    return result
                }
            }
            return command.onTab(sender, args)
        }
        return listOf()
    }

    private fun hasPermission(sender: CommandSender, command: Command): Boolean {
        return if (command.permission.isEmpty()) true
        else sender.hasPermission(command.permission)
    }

    abstract fun execute(sender: CommandSender, args: List<String>)
    open fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return emptyList()
    }
}
