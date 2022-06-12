package work.gavenda.yawa.image

import org.bukkit.command.CommandSender
import work.gavenda.yawa.*
import work.gavenda.yawa.api.Command
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.deleteIfExists

class ImageDeleteCommand : Command(
    permission = Permission.IMAGE_DELETE,
    commands = listOf("imgdel")
) {
    private val imageDirectory = File("plugins/Images")

    override fun execute(sender: CommandSender, args: List<String>) {
        if (args.size != 1) return

        val fileNameParam = args[0]
        val fileDestination = Paths.get(imageDirectory.path, fileNameParam)

        scheduler.runTaskAsynchronously(plugin) { _ ->
            try {
                if (fileDestination.deleteIfExists()) {
                    sender.sendMessageUsingKey(Message.ImageDeleteSuccess)
                } else {
                    sender.sendMessageUsingKey(Message.ImageDeleteNotFound)
                }
            } catch (ex: Exception) {
                sender.sendMessageUsingKey(Message.ImageDeleteError)
            }
        }
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return when (args.size) {
            1 -> listOf("<file-name>")
            else -> emptyList()
        }
    }
}