/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 *  Copyright (C) 2021 Gavenda <gavenda@disroot.org>
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
 *
 */

package work.gavenda.yawa.notify

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import work.gavenda.yawa.Config
import work.gavenda.yawa.Message
import work.gavenda.yawa.Messages
import work.gavenda.yawa.api.captilizeFully
import work.gavenda.yawa.api.compat.displayNameCompat
import work.gavenda.yawa.api.compat.loreCompat
import work.gavenda.yawa.api.compat.sendMessageCompat
import work.gavenda.yawa.api.placeholder.Placeholder
import work.gavenda.yawa.api.toPlainText
import work.gavenda.yawa.login.verifiedName
import work.gavenda.yawa.server

/**
 * Notifier for items.
 */
class ItemListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onItemPickedUp(event: EntityPickupItemEvent) {
        if (event.entityType !== EntityType.PLAYER) return

        val player = event.entity as Player
        val itemStack = event.item.itemStack
        val itemStackLore = itemStack.loreCompat()?.first {
            it.toPlainText().contains("Farmed up by")
        }
        val itemStackLoreStr = if (itemStackLore != null) {
            itemStackLore.toPlainText().split("Farmed up by ")[1]
        } else ""

        if (itemStackLore == null) {
            itemStack.loreCompat(
                listOf(
                    Component.text("Farmed up by ").append(player.verifiedName)
                )
            )
        }

        val message = Messages.forPlayer(player)
            .get(Message.NotifyItemPickup)
        val messageFarmed = Messages.forPlayer(player)
            .get(Message.NotifyItemPickupFarmed)
        val materialsToMatch = Config.Notify.Item.map { Material.getMaterial(it) }
        val matches = materialsToMatch.any { it == itemStack.type }

        if (matches) {
            val placeholderParams = mapOf(
                "farmer-name" to player.verifiedName,
                "item-stack-amount" to itemStack.amount.toString(),
                "item-name" to itemStack.type.name
                    .replace("_", " ")
                    .captilizeFully()
            )

            if (itemStackLoreStr.isEmpty()) {
                player.world.sendMessageCompat(
                    Placeholder.withContext(player)
                        .parse(message, placeholderParams)
                )
            } else {
                player.world.sendMessageCompat(
                    Placeholder.withContext(player)
                        .parse(messageFarmed, placeholderParams)
                )
            }
        }
    }
}