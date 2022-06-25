package work.gavenda.yawa.chunk

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import work.gavenda.yawa.Permission
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.HelpList
import work.gavenda.yawa.api.compat.sendMessageCompat

class ChunkCommand : Command() {
    override val commands = listOf("chunk")
    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) return

        HelpList()
            .command("chunk mark", listOf(), "Mark chunk to always keep running", Permission.CHUNK_MARK)
            .command("chunk unmark", listOf(), "Reverts the chunk to default behavior", Permission.CHUNK_UNMARK)
            .generate(sender)
            .forEach { sender.sendMessageCompat(it) }
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return subCommandKeys.toList()
    }
}