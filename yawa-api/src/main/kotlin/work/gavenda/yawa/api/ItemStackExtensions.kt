package work.gavenda.yawa.api

import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

val Enchantment.clientDisplayName
    get() = key.key
        .replace("_", " ")
        .capitalizeFully()

val ItemStack.clientDisplayName
    get() = type.name
        .replace("_", " ")
        .capitalizeFully()