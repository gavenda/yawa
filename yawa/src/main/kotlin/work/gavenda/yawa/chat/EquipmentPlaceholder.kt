package work.gavenda.yawa.chat

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import work.gavenda.yawa.api.placeholder.PlaceholderProvider
import work.gavenda.yawa.api.previewComponent
import work.gavenda.yawa.hiddenarmor.HiddenArmorFeature
import work.gavenda.yawa.hiddenarmor.unhideArmor

class EquipmentPlaceholder : PlaceholderProvider {

    companion object {
        const val HELM = "helm"
        const val CHESTPLATE = "chestplate"
        const val LEGGINGS = "leggings"
        const val BOOTS = "boots"
        const val HAND = "hand"
        const val OFF_HAND = "offhand"
    }

    private fun provideBySlot(player: Player, slot: EquipmentSlot): Component? {
        val itemStack = when(slot) {
            EquipmentSlot.HEAD -> player.inventory.helmet
            EquipmentSlot.CHEST -> player.inventory.chestplate
            EquipmentSlot.LEGS -> player.inventory.leggings
            EquipmentSlot.FEET -> player.inventory.boots
            else -> return null
        }

        if (HiddenArmorFeature.enabled && HiddenArmorFeature.hasPlayer(player)) {
            itemStack?.unhideArmor(slot)
        }

        return itemStack?.previewComponent
    }

    override fun provide(player: Player?, world: World?): Map<String, Component?> {
        if (player == null) return mapOf()

        return mapOf(
            HELM to provideBySlot(player, EquipmentSlot.HEAD),
            CHESTPLATE to provideBySlot(player, EquipmentSlot.CHEST),
            LEGGINGS to provideBySlot(player, EquipmentSlot.LEGS),
            BOOTS to provideBySlot(player, EquipmentSlot.FEET),
            HAND to if (player.inventory.itemInMainHand.type != Material.AIR) {
                player.inventory.itemInMainHand.previewComponent
            } else null,
            OFF_HAND to if (player.inventory.itemInOffHand.type != Material.AIR) {
                player.inventory.itemInOffHand.previewComponent
            } else null
        )
    }
}
