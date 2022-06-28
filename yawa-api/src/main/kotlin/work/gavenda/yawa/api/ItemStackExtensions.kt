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
package work.gavenda.yawa.api

import de.tr7zw.nbtapi.NBTItem
import net.kyori.adventure.key.Key
import net.kyori.adventure.nbt.api.BinaryTagHolder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import work.gavenda.yawa.api.compat.displayNameCompat
import work.gavenda.yawa.api.compat.loreCompat

/**
 * Returns the enchantment's name as displayed in the client.
 */
val Enchantment.clientDisplayName
    get() = key.key
        .replace("_", " ")
        .capitalizeFully()

/**
 * Returns the item stack's name as displayed in the client.
 */
val ItemStack.clientDisplayName
    get() = type.name
        .replace("_", " ")
        .capitalizeFully()

/**
 * Returns the item stack preview.
 */
@Suppress("DEPRECATION")
val ItemStack.previewComponent: Component
    get() {
        val materialName = Component.text(clientDisplayName)
        val displayName = if (hasItemMeta() && itemMeta.hasDisplayName()) {
            itemMeta.displayNameCompat
                ?.color(NamedTextColor.AQUA)
                ?.decorate(TextDecoration.ITALIC)
                ?: Component.empty()
        } else materialName

        itemMeta.loreCompat = listOf(itemDurability)

        val nbtItem = NBTItem(this)
        val itemKey = Key.key(type.key.toString())
        val itemHover = HoverEvent.showItem(itemKey, amount, BinaryTagHolder.binaryTagHolder(nbtItem.asNBTString()))

        return Component.textOfChildren(
            Component.text("[", NamedTextColor.WHITE),
            displayName
                .color(materialColor)
                .hoverEvent(itemHover),
            Component.text("]", NamedTextColor.WHITE)
        )
    }

/**
 * Returns the text color of the material used. Applies to tools, armors, and specific blocks only.
 */
val ItemStack.materialColor: TextColor
    get() {
        val m = type.toString()
        return if (m.startsWith("NETHERITE_"))
            NamedTextColor.LIGHT_PURPLE
        else if (m.startsWith("DIAMOND_"))
            NamedTextColor.AQUA
        else if (m.startsWith("GOLDEN_"))
            NamedTextColor.GOLD
        else NamedTextColor.WHITE
    }

/**
 * Returns the durability percentage of the item if exists.
 */
val ItemStack.durabilityPercentage: Int
    get() {
        if (itemMeta is Damageable) {
            val meta = itemMeta as Damageable
            val maxDurability = type.maxDurability.toInt()
            return if (maxDurability == 0) -1 else 100 - meta.damage * 100 / maxDurability
        }
        return -1
    }

/**
 * Returns the item durability of this item as a component.
 */
val ItemStack.itemDurability: Component
    get() {
        if (durabilityPercentage != -1) {
            val color = if (durabilityPercentage >= 70) {
                NamedTextColor.GREEN
            } else if (durabilityPercentage < 30) {
                NamedTextColor.RED
            } else {
                NamedTextColor.YELLOW
            }

            return Component.textOfChildren(
                Component.text("Durability: ", NamedTextColor.GRAY),
                Component.text("$durabilityPercentage%", TextColor.color(color))
            )
        }
        return Component.empty()
    }
