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

import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import work.gavenda.yawa.*
import work.gavenda.yawa.api.capitalizeFully
import work.gavenda.yawa.api.compat.sendMessageCompat
import work.gavenda.yawa.api.placeholder.Placeholders

/**
 * Notifier for items.
 */
class ItemListener : Listener {

    private val isDiscordSRVEnabled = pluginManager.getPlugin("DiscordSRV") != null

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onItemPickedUp(event: EntityPickupItemEvent) {
        if (event.entityType !== EntityType.PLAYER) return

        val player = event.entity as Player
        val itemStack = event.item.itemStack

        val pickupMessage = Messages.forPlayer(player)
            .get(Message.NotifyItemPickup)
        val materialsToMatch = Config.Notify.Item.map { Material.getMaterial(it) }
        val matches = materialsToMatch.any { it == itemStack.type }

        if (matches) {
            val placeholderParams = mapOf(
                "item-stack-amount" to itemStack.amount.toString(),
                "item-name" to itemStack.type.name
                    .replace("_", " ")
                    .capitalizeFully()
            )

            val message = Placeholders.withContext(player)
                .parse(pickupMessage, placeholderParams)

            player.world.sendMessageCompat(message)
            player.discordAlert(message)
        }
    }
}