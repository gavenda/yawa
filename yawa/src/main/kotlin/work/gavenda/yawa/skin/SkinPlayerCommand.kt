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

package work.gavenda.yawa.skin

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.*
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.applySkin
import work.gavenda.yawa.api.mojang.MOJANG_KEY_TEXTURES
import work.gavenda.yawa.api.mojang.MojangApi
import work.gavenda.yawa.api.mojang.MojangProfileProperty
import work.gavenda.yawa.api.mojang.RateLimitException

/**
 * Applies a skin from an existing minecraft account name.
 */
class SkinPlayerCommand : Command() {
    override val permission = Permission.SKIN_PLAYER
    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) return

        if (args.size == 1) {
            val name = args[0]

            sender.sendMessageUsingKey(Message.SkinRetrieve)

            scheduler.runTaskAsynchronously(plugin) { _ ->
                try {
                    val uuid = MojangApi.findUuidByName(name)
                    if (uuid != null) {
                        MojangApi.findProfile(uuid)?.let { playerProfile ->
                            playerProfile.properties
                                .find { it.name == MOJANG_KEY_TEXTURES }
                                ?.let { texture -> applyAndSaveSkin(sender, texture) }
                        }
                    } else {
                        sender.sendMessageUsingKey(Message.SkinNotFound)
                    }
                } catch (e: RateLimitException) {
                    sender.sendMessageUsingKey(Message.SkinRateLimit)
                }
            }
        }
    }

    private fun applyAndSaveSkin(player: Player, texture: MojangProfileProperty) {
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
            1 -> listOf("<player>")
            else -> emptyList()
        }
    }
}