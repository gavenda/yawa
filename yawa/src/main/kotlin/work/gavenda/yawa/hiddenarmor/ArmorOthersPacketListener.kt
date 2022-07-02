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