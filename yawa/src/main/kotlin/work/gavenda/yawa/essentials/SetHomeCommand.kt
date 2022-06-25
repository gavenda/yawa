package work.gavenda.yawa.essentials

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.Permission
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.compat.sendMessageCompat
import work.gavenda.yawa.plugin
import work.gavenda.yawa.scheduler

class SetHomeCommand : Command() {
    override val permission = Permission.ESSENTIALS_HOME_SET

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

            val location = "[${sender.location.blockX}, ${sender.location.blockY}, ${sender.location.blockZ}]"

            sender.sendMessageCompat(
                Component.text("Your home has been set at ", NamedTextColor.YELLOW)
                    .append(Component.text(location, NamedTextColor.WHITE))
            )
        }
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return emptyList()
    }
}