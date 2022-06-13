package work.gavenda.yawa.hiddenarmor

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.PlayerInventory
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
            scheduler.runTaskLater(plugin, { _ ->
                player.updateHiddenArmorSelf()
            }, 1L)
        }
    }
}