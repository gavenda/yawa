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

package work.gavenda.yawa.imgup

import org.bukkit.command.CommandSender
import work.gavenda.yawa.*
import work.gavenda.yawa.api.Command
import java.io.File
import java.net.URL
import java.nio.file.Paths

class ImageUploadCommand : Command(
    permission = Permission.IMAGE_UPLOAD,
    commands = listOf("imgup")
) {

    private val imageDirectory = File("plugins/Images")
    private val allowedFileExt = Regex("(.*?)\\.(jpg|gif|jpeg|png)\$", RegexOption.IGNORE_CASE)

    override fun execute(sender: CommandSender, args: List<String>) {
        if (args.size != 2) return

        val urlParam = args[0]
        val fileNameParam = args[1]

        // Validate
        val url = URL(urlParam)

        if (allowedFileExt.containsMatchIn(fileNameParam).not()) {
            sender.sendMessageUsingKey(Message.ImageUploadInvalid)
            return
        }

        val fileDestination = Paths.get(imageDirectory.path, fileNameParam).toFile()

        scheduler.runTaskAsynchronously(plugin) { _ ->
            try {
                sender.sendMessageUsingKey(Message.ImageUploadBegin)
                url.downloadTo(fileDestination)
                sender.sendMessageUsingKey(Message.ImageUploadSuccess)
            } catch (ex: Exception) {
                sender.sendMessageUsingKey(Message.ImageUploadError)
            }
        }
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return when (args.size) {
            1 -> listOf("<url>")
            2 -> listOf("<file-name>")
            else -> emptyList()
        }
    }
}