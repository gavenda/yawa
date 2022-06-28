package work.gavenda.yawa.essentials

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.*
import work.gavenda.yawa.api.Command

class SetHomeCommand : Command() {
    override val permission = Permission.ESSENTIALS_HOME_SET
    override val commands = listOf("sethome", "sh")

    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) return

        scheduler.runTaskAsynchronously(plugin) { _ ->
            transaction {
                val playerHome = PlayerHomeDb.findById(sender.uniqueId) ?: PlayerHomeDb.new(sender.uniqueId) {}

                playerHome.apply {
                    world = sender.location.world.uid
                    x = sender.location.blockX
                    y = sender.location.blockY
                    z = sender.location.blockZ
                }
            }

            sender.sendMessageUsingKey(Message.EssentialsHomeSet)
        }
    }
}