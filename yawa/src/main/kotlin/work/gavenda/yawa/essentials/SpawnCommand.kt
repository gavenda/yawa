package work.gavenda.yawa.essentials

import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import work.gavenda.yawa.*
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.compat.getChunkAtAsyncCompat
import work.gavenda.yawa.api.compat.teleportAsyncCompat

class TeleportSpawnCommand : Command() {
    override val permission = Permission.ESSENTIALS_TELEPORT_SPAWN

    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) return
        val world = server.worlds.firstOrNull {
            it.environment == World.Environment.NORMAL
        }
        if (world != null) {
            world.getChunkAtAsyncCompat(world.spawnLocation).thenAccept {
                sender.teleportAsyncCompat(world.spawnLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept {
                    sender.sendMessageUsingKey(Message.EssentialsTeleportSpawn)
                }
            }
        } else {
            sender.sendMessageUsingKey(Message.EssentialsTeleportErrorNoOverworld)
            logger.warn("Unable to find overworld!")
        }
    }
}