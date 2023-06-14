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
import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import work.gavenda.yawa.api.capitalizeFully
import work.gavenda.yawa.api.itemDurability

/**
 * Return an item stack as an armor placeholder.
 */
@Suppress("DEPRECATION")
fun ItemStack.asArmorPlaceholder(): ItemStack {
    if (type == Material.AIR) return this
    if (type == Material.ELYTRA) return this

    // Getting item meta and lore
    val itemMeta = itemMeta.clone()
    val lore: List<Component> = itemMeta.clone().lore() ?: listOf()

    // Applying item meta and lore
    itemMeta.lore(lore + itemDurability)

    // Changing armor material and name to its placeholder's, if it has one
    val button = hiddenArmorMaterial
    if (button != null) {
        itemMeta.displayName(hiddenArmorName)
        type = button
    }

    setItemMeta(itemMeta)
    return this
}

fun ItemStack.unhideArmor(slot: EquipmentSlot) {
    val postfix = when (slot) {
        EquipmentSlot.HEAD -> "HELMET"
        EquipmentSlot.CHEST -> "CHESTPLATE"
        EquipmentSlot.LEGS -> "LEGGINGS"
        EquipmentSlot.FEET -> "BOOTS"
        else -> ""
    }
    val prefix = when (type) {
        Material.POLISHED_BLACKSTONE_BUTTON -> "NETHERITE_"
        Material.WARPED_BUTTON -> "DIAMOND_"
        Material.BIRCH_BUTTON -> "GOLDEN_"
        Material.STONE_BUTTON -> "IRON_"
        Material.ACACIA_BUTTON -> "LEATHER_"
        Material.JUNGLE_BUTTON -> "CHAINMAIL_"
        Material.CRIMSON_BUTTON -> "TURTLE_"
        else -> ""
    }
    if (prefix.isNotBlank() && postfix.isNotBlank()) {
        type = Material.valueOf(prefix + postfix)
    }
}

val ItemStack.hiddenArmorMaterial: Material?
    get() {
        if (!armor) return null
        val m = type.toString()

        return if (m.startsWith("NETHERITE_"))
            Material.POLISHED_BLACKSTONE_BUTTON
        else if (m.startsWith("DIAMOND_"))
            Material.WARPED_BUTTON
        else if (m.startsWith("GOLDEN_"))
            Material.BIRCH_BUTTON
        else if (m.startsWith("IRON_"))
            Material.STONE_BUTTON
        else if (m.startsWith("LEATHER_"))
            Material.ACACIA_BUTTON
        else if (m.startsWith("CHAINMAIL_"))
            Material.JUNGLE_BUTTON
        else if (m.startsWith("TURTLE_"))
            Material.CRIMSON_BUTTON
        else null
    }

val ItemStack.armor: Boolean
    get() {
        val type = type.name
        return (type.endsWith("_HELMET")
                || type.endsWith("_CHESTPLATE")
                || type.endsWith("_LEGGINGS")
                || type.endsWith("_BOOTS"))
    }

val ItemStack.hiddenArmorName: Component?
    get() {
        val name = type.name
            .replace("_", " ")
            .capitalizeFully()

        return if (itemMeta.hasDisplayName()) {
            itemMeta.displayName()
                ?.append(Component.text(" "))
                ?.append(Component.text("(Hidden)", NamedTextColor.DARK_GRAY))
        } else {
            Component.text(name, NamedTextColor.WHITE)
                .append(Component.text(" "))
                .append(Component.text("(Hidden)", NamedTextColor.DARK_GRAY))
        }
    }
