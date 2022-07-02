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

package work.gavenda.yawa.skin

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.*
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.applySkin
import work.gavenda.yawa.api.mineskin.MineSkinApi
import work.gavenda.yawa.api.mineskin.MineSkinTexture
import work.gavenda.yawa.api.mojang.RateLimitException
import java.net.URI
import java.net.URISyntaxException

/**
 * Applies a skin from a texture url.
 */
class SkinUrlCommand : Command() {
    override val permission = Permission.SKIN_URL
    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) return

        if (args.isNotEmpty()) {
            try {
                val url = URI(args[0])
                val slim = if (args.size == 2) args[1].toBoolean() else false
                val validScheme = url.scheme == "http" || url.scheme == "https"

                if (!validScheme) {
                    reject(sender)
                    return
                }

                sender.sendMessageUsingKey(Message.SkinGenerate)

                scheduler.runTaskAsynchronously(plugin) { _ ->
                    try {
                        val texture = MineSkinApi.generateTexture(url, slim)
                        applyAndSaveSkin(sender, texture)
                    } catch (e: RateLimitException) {
                        sender.sendMessageUsingKey(Message.SkinRateLimit)
                    }
                }
            } catch (e: URISyntaxException) {
                reject(sender)
            }
        }
    }

    private fun reject(player: Player) {
        logger.warn("${player.name} sent an invalid url.")
        player.sendMessageUsingKey(Message.SkinReject)
    }

    private fun applyAndSaveSkin(player: Player, texture: MineSkinTexture) {
        player.applySkin(texture.value, texture.signature)

        transaction {
            val playerTexture = PlayerTexture.findById(player.uniqueId) ?: PlayerTexture.new(player.uniqueId) {}

            playerTexture.texture = texture.value
            playerTexture.signature = texture.signature
        }

        player.sendMessageUsingKey(Message.SkinApplied)
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return when (args.size) {
            1 -> listOf("<url>")
            2 -> listOf("true")
            else -> emptyList()
        }
    }
}