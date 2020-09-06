package work.gavenda.yawa.skin

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.restoreSkin

class SkinResetCommand : Command("yawa.skin.reset") {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) return
        sender.restoreSkin()

        transaction {
            PlayerTexture.findById(sender.uniqueId)?.delete()
        }
    }

    override fun onTab(sender: CommandSender, args: Array<String>): List<String>? {
        return null
    }
}