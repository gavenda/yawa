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
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.GameMode
import org.bukkit.inventory.ItemStack
import work.gavenda.yawa.plugin

/**
 * Hides armor from yourself.
 */
class ArmorSelfPacketListener : PacketAdapter(
    params()
        .plugin(plugin)
        .listenerPriority(ListenerPriority.HIGH)
        .types(PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS)
) {

    override fun onPacketSending(event: PacketEvent) {
        val packet = event.packet
        val player = event.player

        if (HiddenArmorFeature.shouldNotHide(player) || player.gameMode == GameMode.CREATIVE) return

        val windowId = packet.integers.read(0)

        // SET_SLOT - Change with placeholder on equip
        if (packet.type == PacketType.Play.Server.SET_SLOT && windowId == 0) {
            val slot = packet.integers.read(2)
            if (slot in 5..8) {
                val itemStack = packet.itemModifier.read(0)
                if (itemStack != null) {
                    packet.itemModifier.write(0, itemStack.asArmorPlaceholder())
                }
            }
        }

        // WINDOW_ITEMS - Change item with placeholder on equipped slot
        if (packet.type == PacketType.Play.Server.WINDOW_ITEMS && windowId == 0) {
            val itemStacks = packet.itemListModifier.read(0)
            itemStacks.subList(5, 9).forEach { itemStack: ItemStack ->
                itemStack.itemMeta = itemStack.asArmorPlaceholder().itemMeta
            }
        }
    }
}