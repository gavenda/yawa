package work.gavenda.yawa.skin

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.Plugin
import work.gavenda.yawa.api.applySkin
import work.gavenda.yawa.api.bukkitAsyncTask
import work.gavenda.yawa.api.bukkitTask
import work.gavenda.yawa.api.mojang.MOJANG_VAL_TEXTURES
import work.gavenda.yawa.api.mojang.MojangAPI
import work.gavenda.yawa.api.mojang.MojangProfileProperty

/**
 * Applies a skin from an existing minecraft account name.
 */
class SkinPlayerCommand : Command("yawa.skin") {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) return

        if (args.size == 1) {
            val name = args[0]

            bukkitAsyncTask(Plugin.Instance) {
                MojangAPI.findUuidByUsername(name)?.let { uuid ->
                    MojangAPI.findProfile(uuid)?.let { playerProfile ->
                        playerProfile.properties
                            .find { it.name == MOJANG_VAL_TEXTURES }
                            ?.let { texture -> applySkin(sender, texture) }
                    }
                }
            }
        }
    }

    private fun applySkin(player: Player, texture: MojangProfileProperty) = bukkitTask(Plugin.Instance) {
        player.applySkin(texture.value, texture.signature)
    }

    override fun onTab(sender: CommandSender, args: Array<String>): List<String>? {
        return null
    }
}