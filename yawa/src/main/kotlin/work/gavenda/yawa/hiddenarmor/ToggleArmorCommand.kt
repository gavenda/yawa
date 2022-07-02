/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (c) 2022 Gavenda <gavenda@disroot.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package work.gavenda.yawa.hiddenarmor

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.Message
import work.gavenda.yawa.Permission
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.asAudience
import work.gavenda.yawa.sendMessageUsingKey

class ToggleArmorCommand : Command() {
    override val permission = Permission.TOGGLE_HIDDEN_ARMOR
    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) return

        val isHidden = HiddenArmorFeature.hasPlayer(sender)
        val newHiddenValue = isHidden.not()

        if (isHidden) {
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

        transaction {
            val armorDb = PlayerArmorDb.findById(sender.uniqueId) ?: PlayerArmorDb.new(sender.uniqueId) {}
            armorDb.hidden = newHiddenValue
        }
    }

}