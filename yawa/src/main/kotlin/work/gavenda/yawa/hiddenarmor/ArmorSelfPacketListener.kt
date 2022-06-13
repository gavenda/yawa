package work.gavenda.yawa.hiddenarmor

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.inventory.ItemStack
import work.gavenda.yawa.plugin

class ArmorSelfPacketListener : PacketAdapter(
    params()
        .plugin(plugin)
        .listenerPriority(ListenerPriority.HIGH)
        .types(PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS)
) {

    override fun onPacketSending(event: PacketEvent) {
        val packet = event.packet
        val player = event.player

        if (HiddenArmorFeature.shouldNotHide(player)) return

        // SET_SLOT
        val windowId = packet.integers.read(0)

        if (packet.type == PacketType.Play.Server.SET_SLOT && windowId == 0) {
            val slot = packet.integers.read(2)
            if (slot in 5..8) {
                val itemStack = packet.itemModifier.read(0)
                if (itemStack != null) {
                    packet.itemModifier.write(0, itemStack.hideArmor())
                }
            }
        }

        // WINDOW_ITEMS
        if (packet.type == PacketType.Play.Server.WINDOW_ITEMS && windowId == 0) {
            val itemStacks = packet.itemListModifier.read(0)
            itemStacks.subList(5, 8).forEach { itemStack: ItemStack ->
                itemStack.itemMeta = itemStack.hideArmor().itemMeta
            }
        }
    }
}