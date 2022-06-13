package work.gavenda.yawa.hiddenarmor

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import work.gavenda.yawa.Message
import work.gavenda.yawa.Permission
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.asAudience
import work.gavenda.yawa.sendMessageUsingKey

class ToggleArmorCommand : Command(
    permission = Permission.TOGGLE_HIDDEN_ARMOR,
    commands = listOf("toggle-armor")
) {
    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) return

        if (HiddenArmorFeature.hasPlayer(sender)) {
            HiddenArmorFeature.removeHiddenPlayer(sender)
            sender.sendMessageUsingKey(Message.HiddenArmorVisible)
            sender.asAudience().sendActionBar(
                Component.text("Armor Visibility: ")
                    .append(Component.text("ON", NamedTextColor.GREEN))
            )
        } else {
            HiddenArmorFeature.addHiddenPlayer(sender)
            sender.sendMessageUsingKey(Message.HiddenArmorInvisible)
            sender.asAudience().sendActionBar(
                Component.text("Armor Visibility: ")
                    .append(Component.text("OFF", NamedTextColor.RED))
            )
        }

        sender.updateHiddenArmor()
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return emptyList()
    }

}