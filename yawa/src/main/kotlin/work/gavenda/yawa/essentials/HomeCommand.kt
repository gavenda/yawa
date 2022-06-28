package work.gavenda.yawa.essentials

import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.*
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.compat.teleportAsyncCompat

class HomeCommand : Command() {
    override val permission = Permission.ESSENTIALS_HOME_TELEPORT
    override val commands = listOf("home")

    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) return

        scheduler.runTaskAsynchronously(plugin) { _ ->
            transaction {
                val playerHomeDb = PlayerHomeDb.findById(sender.uniqueId)

                if (playerHomeDb != null) {
                    val world = server.getWorld(playerHomeDb.world)

                    if (world == null) {
                        sender.sendMessageUsingKey(Message.EssentialsTeleportErrorNoHomeWorld)
                        playerHomeDb.delete()
                        return@transaction
                    }

                    val x = playerHomeDb.x.toDouble()
                    val y = playerHomeDb.y.toDouble()
                    val z = playerHomeDb.z.toDouble()
                    val location = Location(world, x, y, z)

                    scheduler.runTask(plugin) { _ ->
                        world.getChunkAtAsync(location).thenAccept {
                            sender.teleportAsyncCompat(location, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept {
                                sender.sendMessageUsingKey(Message.EssentialsTeleportHome)

                            }
                        }
                    }
                } else {
                    sender.sendMessageUsingKey(Message.EssentialsTeleportErrorNoHome)
                }
            }
        }
    }
}