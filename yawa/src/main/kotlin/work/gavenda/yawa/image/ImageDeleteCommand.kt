package work.gavenda.yawa.image

import org.bukkit.command.CommandSender
import work.gavenda.yawa.*
import work.gavenda.yawa.api.Command
import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

class ImageDeleteCommand : Command(
    permission = Permission.IMAGE_DELETE,
    commands = listOf("imgdel")
) {
    private val imageDirectory = Path("plugins/Images")

    override fun execute(sender: CommandSender, args: List<String>) {
        if (args.size != 1) return

        val fileNameParam = args[0]
        val fileDestination = imageDirectory.resolve(fileNameParam)

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
            1 -> imageDirectory
                .listDirectoryEntries()
                .map { it.name }
            else -> emptyList()
        }
    }
}