package work.gavenda.yawa.chat

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import work.gavenda.yawa.api.capitalizeFully
import work.gavenda.yawa.api.compat.displayNameCompat
import work.gavenda.yawa.api.placeholder.PlaceholderProvider
import work.gavenda.yawa.hiddenarmor.HiddenArmorFeature
import work.gavenda.yawa.hiddenarmor.durabilityPercentage
import work.gavenda.yawa.hiddenarmor.itemDurability
import work.gavenda.yawa.hiddenarmor.unhideArmor

class EquipmentPlaceholder : PlaceholderProvider {

    override fun provide(player: Player?, world: World?): Map<String, Component?> {
        if (player == null) return mapOf()

        val helmItemStack = player.inventory.helmet?.clone()
        val chestplateItemStack = player.inventory.chestplate?.clone()
        val leggingsItemStack = player.inventory.leggings?.clone()
        val bootsItemStack = player.inventory.boots?.clone()

        if (HiddenArmorFeature.enabled && HiddenArmorFeature.hasPlayer(player)) {
            helmItemStack?.unhideArmor(EquipmentSlot.HEAD)
            chestplateItemStack?.unhideArmor(EquipmentSlot.CHEST)
            leggingsItemStack?.unhideArmor(EquipmentSlot.LEGS)
            bootsItemStack?.unhideArmor(EquipmentSlot.FEET)
        }

        val helm = helmItemStack?.previewComponent
        val chestplate = chestplateItemStack?.previewComponent
        val leggings = leggingsItemStack?.previewComponent
        val boots = bootsItemStack?.previewComponent
        val hand = if (player.inventory.itemInMainHand.type != Material.AIR) {
            player.inventory.itemInMainHand.previewComponent
        } else null
        val offhand = if (player.inventory.itemInOffHand.type != Material.AIR) {
            player.inventory.itemInOffHand.previewComponent
        } else null

        return mapOf(
            "helm" to helm,
            "chestplate" to chestplate,
            "leggings" to leggings,
            "boots" to boots,
            "hand" to hand,
            "offhand" to offhand
        )
    }
}

val ItemStack.previewComponent: Component
    get() {
        val materialName = Component.text(
            type.name.replace("_", " ").capitalizeFully()
        )
        val displayName = if (hasItemMeta() && itemMeta.hasDisplayName()) {
            itemMeta.displayNameCompat
                ?.color(NamedTextColor.AQUA)
                ?.decorate(TextDecoration.ITALIC)
                ?: Component.empty()
        } else materialName
        val displayNameHover = HoverEvent.showText(
            Component.text { builder ->
                builder.append(displayName)

                if (hasItemMeta() && itemMeta.hasDisplayName()) {
                    builder.append(Component.newline())
                    builder.append(materialName)
                }

                if (durabilityPercentage != -1) {
                    builder.append(Component.newline())
                    builder.append(itemDurability)
                }

                if (enchantments.isNotEmpty()) {
                    builder.append(Component.newline())
                    enchantments.forEach { (enchantment, level) ->
                        builder.append(Component.newline())
                        builder.append(
                            Component.text(
                                "${enchantment.key.key.capitalizeFully()} $level",
                                NamedTextColor.GRAY
                            )
                        )
                    }
                }
            }
        )

        return Component.text("[", NamedTextColor.WHITE)
            .append(
                displayName
                    .color(materialColor)
                    .hoverEvent(displayNameHover)
            )
            .append(Component.text("]", NamedTextColor.WHITE))
    }


val ItemStack.materialColor: TextColor
    get() {
        val m = type.toString()
        return if (m.startsWith("NETHERITE_"))
            NamedTextColor.DARK_PURPLE
        else if (m.startsWith("DIAMOND_"))
            NamedTextColor.AQUA
        else if (m.startsWith("GOLDEN_"))
            NamedTextColor.GOLD
        else NamedTextColor.WHITE
    }