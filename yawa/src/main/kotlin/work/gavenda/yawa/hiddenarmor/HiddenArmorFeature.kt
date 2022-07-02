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

import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.*
import java.util.*

object HiddenArmorFeature : PluginFeature {
    override val disabled: Boolean
        get() = Config.HiddenArmor.Disabled

    private val hiddenPlayers = mutableListOf<UUID>()
    private val ignoredPlayers = mutableListOf<UUID>()

    private val inventoryShiftClickListener = InventoryShiftClickListener()
    private val potionEffectListener = PotionEffectListener()
    private val gameModeListener = GameModeListener()
    private val toggleArmorCommand = ToggleArmorCommand()

    private val armorOthersPacketListener = ArmorOthersPacketListener()
    private val armorSelfPacketListener = ArmorSelfPacketListener()

    override fun registerEventListeners() {
        protocolManager.addPacketListener(armorOthersPacketListener)
        protocolManager.addPacketListener(armorSelfPacketListener)

        pluginManager.registerEvents(inventoryShiftClickListener)
        pluginManager.registerEvents(potionEffectListener)
        pluginManager.registerEvents(gameModeListener)
    }

    override fun unregisterEventListeners() {
        pluginManager.unregisterEvents(gameModeListener)
        pluginManager.unregisterEvents(potionEffectListener)
        pluginManager.unregisterEvents(inventoryShiftClickListener)

        protocolManager.removePacketListener(armorSelfPacketListener)
        protocolManager.removePacketListener(armorOthersPacketListener)
    }

    override fun enableCommands() {
        plugin.getCommand(Command.TOGGLE_ARMOR)?.setExecutor(toggleArmorCommand)
    }

    override fun disableCommands() {
        plugin.getCommand(Command.TOGGLE_ARMOR)?.setExecutor(DisabledCommand)
    }

    override fun registerPaperEventListeners() {
        pluginManager.registerEvents(toggleArmorCommand)
    }

    override fun unregisterPaperEventListeners() {
        pluginManager.unregisterEvents(toggleArmorCommand)
    }

    override fun createTables() {
        transaction {
            SchemaUtils.create(
                PlayerArmorSchema,
            )
        }
    }

    override fun onEnable() {
        transaction {
            hiddenPlayers.addAll(PlayerArmorDb.all().filter { it.hidden }.map { it.id.value })
        }
    }

    fun shouldNotHide(player: Player): Boolean {
        return !hasPlayer(player) || player.isInvisible || ignoredPlayers.contains(player.uniqueId)
    }

    fun addHiddenPlayer(player: Player) {
        hiddenPlayers.add(player.uniqueId)
    }

    fun removeHiddenPlayer(player: Player) {
        hiddenPlayers.remove(player.uniqueId)
    }

    fun hasPlayer(player: Player): Boolean {
        return hiddenPlayers.contains(player.uniqueId)
    }

    fun addIgnoredPlayer(player: Player) {
        ignoredPlayers.add(player.uniqueId)
    }

    fun removeIgnoredPlayer(player: Player) {
        ignoredPlayers.remove(player.uniqueId)
    }
}