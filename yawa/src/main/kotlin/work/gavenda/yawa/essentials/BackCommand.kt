package work.gavenda.yawa.essentials

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import work.gavenda.yawa.Message
import work.gavenda.yawa.Permission
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.compat.teleportAsyncCompat
import work.gavenda.yawa.sendMessageUsingKey

class BackCommand : Command() {
    override val permission = Permission.ESSENTIALS_TELEPORT_DEATH
    override val commands = listOf("back", "b")
    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) return

        val lastDeathLocation = sender.lastDeathLocation

        if (lastDeathLocation != null) {
            sender.teleportAsyncCompat(lastDeathLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept {
                sender.sendMessageUsingKey(Message.EssentialsTeleportDeath)
            }
        } else {
            sender.sendMessageUsingKey(Message.EssentialsTeleportErrorNoDeath)
        }
    }
}