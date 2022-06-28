package work.gavenda.yawa.essentials

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.*
import work.gavenda.yawa.api.Command

class SetLocationCommand: Command() {
    override val permission = Permission.ESSENTIALS_LOCATION_SET
    override val commands = listOf("setlocation", "sl")
    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) return
        if (args.isEmpty()) return

        val locationName = args[0]

        scheduler.runTaskAsynchronously(plugin) { _ ->
            transaction {
                val playerLocation = PlayerLocationDb
                    .find { (PlayerLocationSchema.playerUuid eq sender.uniqueId) and (PlayerLocationSchema.name eq locationName) }
                    .firstOrNull() ?: PlayerLocationDb.new() {}

                playerLocation.apply {
                    playerUuid = sender.uniqueId
                    name = locationName
                    world = sender.location.world.uid
                    x = sender.location.blockX
                    y = sender.location.blockY
                    z = sender.location.blockZ
                }
            }

            sender.sendMessageUsingKey(Message.EssentialsLocationSet, mapOf(
                "location-name" to Component.text(locationName, NamedTextColor.WHITE)
            ))
        }
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return listOf("<location-name>")
    }
}