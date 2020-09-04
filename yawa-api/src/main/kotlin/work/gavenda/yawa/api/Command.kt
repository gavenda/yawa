package work.gavenda.yawa.api

import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import java.util.*
import kotlin.collections.HashMap

abstract class Command(private val permission: String = "") : TabExecutor {
    private val subCommands: MutableMap<String, Command> = HashMap()

    val subCommandKeys: Set<String>
        get() = subCommands.keys

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
        executeSubCommands(sender, this, args, cmd.permissionMessage)
        return true
    }

    private fun executeSubCommands(
        sender: CommandSender,
        parent: Command,
        args: Array<String>,
        noPermMessage: String?
    ) {
        for (arg in args) {
            val cmd = parent.subCommands[arg]
            if (cmd != null) {
                executeSubCommands(sender, cmd, args.copyOfRange(1, args.size), noPermMessage)
                return
            }
        }
        if (!hasPermission(sender, parent)) {
            if (noPermMessage != null) {
                sender.sendMessage(noPermMessage)
            }
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
        return if (!hasPermission(sender, this)) null else tooltips(sender, this, args)
    }

    private fun tooltips(sender: CommandSender, command: Command, args: Array<String>): List<String>? {
        for (arg in args) {
            val cmd = command.subCommands[arg]
            if (cmd != null) {
                return tooltips(sender, cmd, args.copyOfRange(1, args.size))
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
        return null
    }

    private fun hasPermission(sender: CommandSender, command: Command): Boolean {
        return if (command.permission.isEmpty()) true
        else sender.hasPermission(command.permission)
    }

    abstract fun execute(sender: CommandSender, args: Array<String>)
    abstract fun onTab(sender: CommandSender, args: Array<String>): List<String>?
}
