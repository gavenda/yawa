package work.gavenda.yawa.skin

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.api.*
import work.gavenda.yawa.api.mineskin.MineSkinApi
import work.gavenda.yawa.api.mineskin.MineSkinTexture
import work.gavenda.yawa.api.mojang.RateLimitException
import work.gavenda.yawa.logger
import java.net.URI
import java.net.URISyntaxException

/**
 * Applies a skin from a texture url.
 */
class SkinUrlCommand : Command("yawa.skin.url") {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) return

        if (args.isNotEmpty()) {
            try {


                val url = URI(args[0])
                val slim = if(args.size == 2) args[1].toBoolean() else false
                val validScheme = url.scheme == "http" || url.scheme == "https"

                if (!validScheme) {
                    reject(sender)
                    return
                }

                sender.sendWithColor("&eGenerating skin please wait...")

                bukkitAsyncTask(Plugin.Instance) {
                    try {
                        val texture = MineSkinApi.generateTexture(url, slim)
                        applyAndSaveSkin(sender, texture)
                    } catch (e: RateLimitException) {
                        sender.sendWithColor("&cWe have reached our rate limits for changing skins, please try again later.")
                    }
                }
            } catch (e: URISyntaxException) {
                reject(sender)
            }
        }
    }

    private fun reject(player: Player) {
        logger.warn("${player.name} sent an invalid url.")
        player.sendWithColor("&cPlease pass a valid url that starts with http or https.")
    }

    private fun applyAndSaveSkin(player: Player, texture: MineSkinTexture) {
        player.applySkin(texture.value, texture.signature)

        transaction {
            val playerTexture = PlayerTexture.findById(player.uniqueId) ?: PlayerTexture.new(player.uniqueId) {}

            playerTexture.texture = texture.value
            playerTexture.signature = texture.signature
        }

        player.sendWithColor("&eSkin successfully applied.")
    }

    override fun onTab(sender: CommandSender, args: Array<String>): List<String>? {
        return when (args.size) {
            1 -> listOf("<url>")
            2 -> listOf("true")
            else -> null
        }
    }
}