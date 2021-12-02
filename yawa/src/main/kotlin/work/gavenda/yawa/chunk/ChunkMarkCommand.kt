package work.gavenda.yawa.chunk

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import work.gavenda.yawa.Message
import work.gavenda.yawa.Permission
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.Placeholder
import work.gavenda.yawa.api.sendMessageCompat
import work.gavenda.yawa.parseWithLocale

class ChunkMarkCommand : Command(Permission.CHUNK_MARK) {

    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) return

        updateChunkMark(sender.location, true).thenRun {
            sender.sendMessageCompat(
                Placeholder.withContext(sender)
                    .parseWithLocale(sender, Message.ChunkMarked)
            )
        }
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return emptyList()
    }
}