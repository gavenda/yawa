/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (c) 2023 Gavenda <gavenda@disroot.org>
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

package work.gavenda.yawa.api

import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.PlayerInfoData
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedGameProfile
import org.bukkit.WorldType
import org.bukkit.entity.Player
import work.gavenda.yawa.api.wrapper.WrapperPlayServerPlayerInfo
import work.gavenda.yawa.api.wrapper.WrapperPlayServerPosition
import work.gavenda.yawa.api.wrapper.WrapperPlayServerRespawn

object SpigotSkinRefresher {
    @Suppress("DEPRECATION", "UNUSED")
    fun refresh(player: Player) {
        val wrappedGameProfile = WrappedGameProfile.fromPlayer(player)
        val enumGameMode = EnumWrappers.NativeGameMode.fromBukkit(player.gameMode)
        val displayName = WrappedChatComponent.fromText(player.playerListName)
        val playerInfoData = PlayerInfoData(wrappedGameProfile, 0, enumGameMode, displayName)

        val removeInfo = WrapperPlayServerPlayerInfo().apply {
            writeAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER)
            writeData(listOf(playerInfoData))
        }
        val addInfo = WrapperPlayServerPlayerInfo().apply {
            writeAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER)
            writeData(listOf(playerInfoData))
        }
        val respawn = WrapperPlayServerRespawn().apply {
            writeResourceKey(player.world)
            writeDimensionTypes(player.world)
            writeGameMode(EnumWrappers.NativeGameMode.fromBukkit(player.gameMode))
            writePreviousGameMode(EnumWrappers.NativeGameMode.fromBukkit(player.previousGameMode ?: player.gameMode))
            writeSeed(player.world.seed)
            writeIsDebug(player.world.debugMode)
            writeIsWorldFlat(player.world.worldType == WorldType.FLAT)
            // true = teleport like, false = player actually died
            writeCopyMetadata(true)
        }
        val position = WrapperPlayServerPosition().apply {
            writeX(player.location.x)
            writeY(player.location.y)
            writeZ(player.location.z)
            writeYaw(player.location.yaw)
            writePitch(player.location.pitch)
            // send an invalid teleport id in order to let Bukkit ignore the incoming confirm packet
            handle.integers.write(0, -1337)
        }

        // Refresh, in order
        // - removePlayer
        // - addPlayer
        // - respawn
        // - updateAbilities
        // - position
        // - slot
        // - updateScaledHealth
        // - updateInventory
        // - triggerHealthUpdate

        // removePlayer
        removeInfo.sendPacket(player)
        // addPlayer
        addInfo.sendPacket(player)
        // respawn
        respawn.sendPacket(player)
        // updateAbilities

        // position
        position.sendPacket(player)
        // slot

        // updateScaledHealth

        // updateInventory
        player.updateInventory()
        // triggerHealthUpdate

        // trigger update exp
        // player.exp = player.exp
        // triggers updateAbilities
        // player.walkSpeed = player.walkSpeed
        // update inventory
        // update held item
        player.inventory.heldItemSlot = player.inventory.heldItemSlot
        // update scaled health
    }

}