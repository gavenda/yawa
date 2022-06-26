package work.gavenda.yawa.hiddenarmor

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import work.gavenda.yawa.api.capitalizeFully
import work.gavenda.yawa.api.compat.displayNameCompat
import work.gavenda.yawa.api.compat.loreCompat

@Suppress("DEPRECATION")
fun ItemStack.hideArmor(): ItemStack {
    if (type == Material.AIR) return this
    if (type == Material.ELYTRA) return this

    // Getting item meta and lore
    val itemMeta = itemMeta.clone()
    val lore: List<Component> = itemMeta.clone().loreCompat ?: listOf()

    // Applying item meta and lore
    itemMeta.loreCompat = lore + itemDurability

    // Changing armor material and name to its placeholder's, if it has one
    val button = hiddenArmorMaterial
    if (button != null) {
        itemMeta.displayNameCompat = armorName
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

val ItemStack.durabilityPercentage: Int
    get() {
        if (itemMeta is Damageable) {
            val meta = itemMeta as Damageable
            val maxDurability = type.maxDurability.toInt()
            return if (maxDurability == 0) -1 else 100 - meta.damage * 100 / maxDurability
        }
        return -1
    }

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

            return Component.text("Durability: ", NamedTextColor.GRAY)
                .append(Component.text("$durabilityPercentage%", TextColor.color(color)))
        }
        return Component.empty()
    }

val ItemStack.armorName: Component?
    get() {
        val name = type.name
            .replace("_", " ")
            .capitalizeFully()

        return if (itemMeta.hasDisplayName()) {
            itemMeta.displayNameCompat
                ?.append(Component.text(" "))
                ?.append(Component.text("(Hidden)", NamedTextColor.DARK_GRAY))
        } else {
            Component.text(name, NamedTextColor.WHITE)
                .append(Component.text(" "))
                .append(Component.text("(Hidden)", NamedTextColor.DARK_GRAY))
        }
    }
