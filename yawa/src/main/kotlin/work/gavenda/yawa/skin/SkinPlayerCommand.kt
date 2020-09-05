package work.gavenda.yawa.skin

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.api.*
import work.gavenda.yawa.api.mojang.MOJANG_VAL_TEXTURES
import work.gavenda.yawa.api.mojang.MojangAPI
import work.gavenda.yawa.api.mojang.MojangProfileProperty
import work.gavenda.yawa.api.mojang.RateLimitException

/**
 * Applies a skin from an existing minecraft account name.
 */
class SkinPlayerCommand : Command("yawa.skin") {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) return

        if (args.size == 1) {
            val name = args[0]

            bukkitAsyncTask(Plugin.Instance) {
                try {
                    val uuid = MojangAPI.findUuidByUsername(name)

                    if (uuid == null) {
                        sender.sendWithColor("&cCannot find premium player with that name.")
                        return@bukkitAsyncTask
                    }

                    MojangAPI.findProfile(uuid)?.let { playerProfile ->
                        playerProfile.properties
                            .find { it.name == MOJANG_VAL_TEXTURES }
                            ?.let { texture -> applyAndSaveSkin(sender, texture) }
                    }
                } catch (e: RateLimitException) {
                    sender.sendWithColor("&cWe have reached our rate limits for changing skins, please try again later.")
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
    }

    override fun onTab(sender: CommandSender, args: Array<String>): List<String>? {
        return null
    }
}