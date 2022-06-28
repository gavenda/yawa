package work.gavenda.yawa.essentials

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.*
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.compat.getChunkAtAsyncCompat
import work.gavenda.yawa.api.compat.teleportAsyncCompat

class LocationCommand : Command() {
    override val permission = Permission.ESSENTIALS_LOCATION_TELEPORT
    override val commands = listOf("location", "loc")

    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) return
        if (args.isEmpty()) return

        val locationName = args[0]

        scheduler.runTaskAsynchronously(plugin) { _ ->
            transaction {
                val playerLocationDb = PlayerLocationDb
                    .find { (PlayerLocationSchema.playerUuid eq sender.uniqueId) and (PlayerLocationSchema.name eq locationName) }
                    .firstOrNull()

                if (playerLocationDb != null) {
                    val world = server.getWorld(playerLocationDb.world)

                    if (world == null) {
                        sender.sendMessageUsingKey(Message.EssentialsTeleportErrorNoHomeWorld)
                        playerLocationDb.delete()
                        return@transaction
                    }

                    val x = playerLocationDb.x.toDouble()
                    val y = playerLocationDb.y.toDouble()
                    val z = playerLocationDb.z.toDouble()
                    val location = Location(world, x, y, z)

                    scheduler.runTask(plugin) { _ ->
                        world.getChunkAtAsyncCompat(location).thenAccept {
                            sender.teleportAsyncCompat(location, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept {
                                sender.sendMessageUsingKey(
                                    Message.EssentialsLocationTeleport, mapOf(
                                        "location-name" to Component.text(playerLocationDb.name, NamedTextColor.WHITE)
                                    )
                                )
                            }
                        }
                    }
                } else {
                    sender.sendMessageUsingKey(Message.EssentialsTeleportErrorNoLocation)
                }
            }
        }
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        if (sender !is Player) return emptyList()
        return when (args.size) {
            0 -> transaction {
                PlayerLocationDb
                    .find { PlayerLocationSchema.playerUuid eq sender.uniqueId }
                    .map { it.name }
            }
            1 -> transaction {
                PlayerLocationDb
                    .find { (PlayerLocationSchema.playerUuid eq sender.uniqueId) and (PlayerLocationSchema.name like args[0] + "%") }
                    .map { it.name }
            }
            else -> emptyList()
        }
    }
}