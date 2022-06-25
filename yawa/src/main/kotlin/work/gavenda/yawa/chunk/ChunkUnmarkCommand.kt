package work.gavenda.yawa.chunk

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import work.gavenda.yawa.*
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.compat.sendMessageCompat
import work.gavenda.yawa.api.placeholder.Placeholders

class ChunkUnmarkCommand : Command() {
    override val permission = Permission.CHUNK_UNMARK
    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) return

        scheduler.runTaskAsynchronously(plugin) { _ ->
            updateChunkMark(sender.location, false)
            sender.sendMessageCompat(
                Placeholders.withContext(sender)
                    .parseWithLocale(sender, Message.ChunkUnmarked)
            )
        }
    }
}