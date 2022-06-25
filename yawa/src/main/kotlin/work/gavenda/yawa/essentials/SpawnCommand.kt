package work.gavenda.yawa.essentials

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import work.gavenda.yawa.*
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.compat.sendMessageCompat
import work.gavenda.yawa.api.compat.teleportAsyncCompat

class TeleportSpawnCommand : Command() {
    override val permission = Permission.ESSENTIALS_TELEPORT_SPAWN

    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) return
        val world = server.worlds.firstOrNull {
            it.environment == World.Environment.NORMAL
        }
        if (world != null) {
            world.getBlockAt(world.spawnLocation)
            sender.teleportAsyncCompat(world.spawnLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenRun {
                sender.sendMessageUsingKey(Message.EssentialsTeleportSpawn)
            }
        } else {
            sender.sendMessageUsingKey(Message.EssentialsTeleportErrorNoOverworld)
            logger.warn("Unable to find overworld!")
        }
    }
}