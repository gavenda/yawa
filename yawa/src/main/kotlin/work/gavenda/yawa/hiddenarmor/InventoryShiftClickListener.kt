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

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.PlayerInventory
import work.gavenda.yawa.api.compat.schedulerCompat
import work.gavenda.yawa.plugin
import work.gavenda.yawa.scheduler

class InventoryShiftClickListener : Listener {

    @EventHandler
    fun onShiftClickArmor(event: InventoryClickEvent) {
        if (!HiddenArmorFeature.hasPlayer(event.whoClicked as Player)) return
        if (event.clickedInventory !is PlayerInventory) return
        if (!event.isShiftClick) return
        val player = event.whoClicked as Player
        val inventory = player.inventory
        val armor = event.currentItem
        val armorType = armor?.type ?: return
        val armorTypeStr = armorType.toString()

        // Checks
        val isElytra = armor.type == Material.ELYTRA
        val isHelmet = armorTypeStr.endsWith("_HELMET") && inventory.helmet == null
        val isChestplate = (armorTypeStr.endsWith("_CHESTPLATE") || isElytra) && inventory.chestplate == null
        val isLeggings = armorTypeStr.endsWith("_LEGGINGS") && inventory.leggings == null
        val isBoots = armorTypeStr.endsWith("_BOOTS") && inventory.boots == null

        if (isHelmet || isChestplate || isLeggings || isBoots) {
            player.schedulerCompat.runAtNextTick(plugin) {
                player.updateHiddenArmorSelf()
            }
        }
    }
}