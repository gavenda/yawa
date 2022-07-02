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

package work.gavenda.yawa.notify

import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantInventory
import work.gavenda.yawa.*
import work.gavenda.yawa.api.capitalizeFully
import work.gavenda.yawa.api.compat.sendMessageCompat
import work.gavenda.yawa.api.placeholder.Placeholders
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Notifier for items.
 */
class ItemListener : Listener {

    private val playerStacks = ConcurrentHashMap<UUID, ConcurrentHashMap<Material, Int>>()

    private val recentLootTask = Runnable {
        playerStacks.forEach { (playerId, loots) ->
            val player = server.getPlayer(playerId)

            if (player == null) {
                playerStacks.remove(playerId)
                return@forEach
            }

            loots.forEach { (material, amount) ->
                val recentPlaceholderParams = mapOf(
                    "item-stack-amount" to amount.toString(),
                    "item-name" to material.name
                        .replace("_", " ")
                        .capitalizeFully()
                )

                val recentPickupMessage = Messages.forPlayer(player)
                    .get(Message.NotifyItemPickupRecent)
                val message = Placeholders.withContext(player)
                    .parse(recentPickupMessage, recentPlaceholderParams)

                player.discordAlert(message)

                loots.remove(material)
            }
            playerStacks.remove(playerId)
        }
    }

    private fun announce(player: Player, itemStack: ItemStack) {
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

            if (itemStack.maxStackSize > 1) {
                val materialMap = playerStacks.computeIfAbsent(player.uniqueId) {
                    ConcurrentHashMap<Material, Int>()
                }
                materialMap.compute(itemStack.type) { _, amount ->
                    (amount ?: 0) + itemStack.amount
                }
                scheduler.runTaskLater(plugin, recentLootTask, 20L * Config.Notify.Debounce)
            } else {
                player.discordAlert(message)
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onItemPickedUp(event: EntityPickupItemEvent) {
        if (event.entityType !== EntityType.PLAYER) return

        val player = event.entity as Player
        val itemStack = event.item.itemStack

        if (event.item.thrower == player.uniqueId) return

        announce(player, itemStack)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onVillagerBuy(event: InventoryClickEvent) {
        if (event.whoClicked !is Player) return
        if (event.inventory.type != InventoryType.MERCHANT) return
        if (event.clickedInventory !is MerchantInventory) return
        if (event.isShiftClick) return

        val inventory = event.inventory as MerchantInventory
        val player = event.whoClicked as Player

        inventory.selectedRecipe?.let { recipe ->
            announce(player, recipe.result)
        }
    }
}