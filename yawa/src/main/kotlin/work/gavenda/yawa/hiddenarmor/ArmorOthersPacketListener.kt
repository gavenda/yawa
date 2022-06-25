package work.gavenda.yawa.hiddenarmor

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import work.gavenda.yawa.plugin
import work.gavenda.yawa.protocolManager

/**
 * Hides armor for other players.
 */
class ArmorOthersPacketListener : PacketAdapter(plugin, PacketType.Play.Server.ENTITY_EQUIPMENT) {

    override fun onPacketSending(event: PacketEvent) {
        val packet = event.packet
        val player = event.player

        val entityId = packet.integers.read(0)
        val hidPlayer = protocolManager.getEntityFromID(player.world, entityId) as? Player ?: return

        if (HiddenArmorFeature.shouldNotHide(hidPlayer)) return

        val pairList = packet.slotStackPairLists.read(0)

        // Make sure we only replace armor slots and non elytra with air.
        pairList
            .filter { it.armorSlot }
            .filter { it.second.type != Material.ELYTRA }
            .forEach { it.second = ItemStack(Material.AIR) }

        packet.slotStackPairLists.write(0, pairList)
    }
}