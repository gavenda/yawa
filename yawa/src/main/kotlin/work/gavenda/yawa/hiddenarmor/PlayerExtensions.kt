package work.gavenda.yawa.hiddenarmor

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.Pair
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import work.gavenda.yawa.protocolManager
import java.lang.reflect.InvocationTargetException

/**
 * Returns true if part of an armor slot.
 */
val Pair<EnumWrappers.ItemSlot, ItemStack>.armorSlot: Boolean
    get() = first == EnumWrappers.ItemSlot.FEET ||
            first == EnumWrappers.ItemSlot.LEGS ||
            first == EnumWrappers.ItemSlot.CHEST ||
            first == EnumWrappers.ItemSlot.HEAD

enum class ArmorType(val value: Int) {
    HELMET(5), CHEST(6), LEGGINGS(7), BOOTS(8), NONE(0);

    companion object {
        fun getType(value: Int): ArmorType {
            for (i in values().indices) {
                if (values()[i].value == value) return values()[i]
            }
            return NONE
        }
    }
}

fun Player.broadcastPacket(packet: PacketContainer) {
    Bukkit.getOnlinePlayers().forEach {
        val valid = it.world == world && it.location.distance(location) < Bukkit.getViewDistance() * 16 && it != player
        try {
            if (valid) {
                protocolManager.sendServerPacket(it, packet)
            }
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }
}

fun PlayerInventory.getArmor(type: ArmorType): ItemStack {
    return when (type) {
        ArmorType.HELMET -> helmet?.clone() ?: ItemStack(Material.AIR)
        ArmorType.CHEST -> chestplate?.clone() ?: ItemStack(Material.AIR)
        ArmorType.LEGGINGS -> leggings?.clone() ?: ItemStack(Material.AIR)
        ArmorType.BOOTS -> boots?.clone() ?: ItemStack(Material.AIR)
        else -> ItemStack(Material.AIR)
    }
}

fun Player.updateHiddenArmor() {
    updateHiddenArmorSelf()
    updateHiddenArmorOthers()
}

fun Player.updateHiddenArmorSelf() {
    for (i in 5..8) {
        val packetSelf = protocolManager.createPacket(PacketType.Play.Server.SET_SLOT)

        packetSelf.integers.write(0, 0)
        packetSelf.integers.write(2, i)

        val armor = inventory.getArmor(ArmorType.getType(i))

        packetSelf.itemModifier.write(0, armor)
        try {
            protocolManager.sendServerPacket(this, packetSelf)
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }
}

fun Player.updateHiddenArmorOthers() {
    val packetOthers = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT)

    packetOthers.integers.write(0, entityId)

    val pairList = packetOthers.slotStackPairLists.read(0)

    pairList.add(Pair(EnumWrappers.ItemSlot.HEAD, inventory.getArmor(ArmorType.HELMET)))
    pairList.add(Pair(EnumWrappers.ItemSlot.CHEST, inventory.getArmor(ArmorType.CHEST)))
    pairList.add(Pair(EnumWrappers.ItemSlot.LEGS, inventory.getArmor(ArmorType.LEGGINGS)))
    pairList.add(Pair(EnumWrappers.ItemSlot.FEET, inventory.getArmor(ArmorType.BOOTS)))

    packetOthers.slotStackPairLists.write(0, pairList)

    broadcastPacket(packetOthers)
}
