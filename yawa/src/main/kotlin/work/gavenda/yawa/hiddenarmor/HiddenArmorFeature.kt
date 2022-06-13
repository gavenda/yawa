package work.gavenda.yawa.hiddenarmor

import org.bukkit.GameMode
import org.bukkit.entity.Player
import work.gavenda.yawa.*
import java.util.*

object HiddenArmorFeature : PluginFeature {
    override val isDisabled: Boolean
        get() = Config.HiddenArmor.Disabled

    private val hiddenPlayers = mutableListOf<String>()
    private val ignoredPlayers = mutableListOf<UUID>()

    private val inventoryShiftClickListener = InventoryShiftClickListener()
    private val entityToggleGlideListener = EntityToggleGlideListener()
    private val potionEffectListener = PotionEffectListener()
    private val gameModeListener = GameModeListener()
    private val toggleArmorCommand = ToggleArmorCommand()

    private val armorOthersPacketListener = ArmorOthersPacketListener()
    private val armorSelfPacketListener = ArmorSelfPacketListener()

    override fun registerEventListeners() {
        protocolManager.addPacketListener(armorOthersPacketListener)
        protocolManager.addPacketListener(armorSelfPacketListener)

        pluginManager.registerEvents(inventoryShiftClickListener)
        pluginManager.registerEvents(entityToggleGlideListener)
        pluginManager.registerEvents(potionEffectListener)
        pluginManager.registerEvents(gameModeListener)
    }

    override fun unregisterEventListeners() {
        pluginManager.unregisterEvents(gameModeListener)
        pluginManager.unregisterEvents(potionEffectListener)
        pluginManager.unregisterEvents(entityToggleGlideListener)
        pluginManager.unregisterEvents(inventoryShiftClickListener)

        protocolManager.removePacketListener(armorSelfPacketListener)
        protocolManager.removePacketListener(armorOthersPacketListener)
    }

    override fun enableCommands() {
        plugin.getCommand("toggle-armor")?.setExecutor(toggleArmorCommand)
    }

    override fun disableCommands() {
        plugin.getCommand("toggle-armor")?.setExecutor(DisabledCommand)
    }

    override fun registerPaperEventListeners() {
        pluginManager.registerEvents(toggleArmorCommand)
    }

    override fun unregisterPaperEventListeners() {
        pluginManager.unregisterEvents(toggleArmorCommand)
    }

    fun shouldNotHide(player: Player): Boolean {
        return !hasPlayer(player) || (player.gameMode == GameMode.CREATIVE) || ignoredPlayers.contains(player.uniqueId)
    }

    fun addHiddenPlayer(player: Player) {
        hiddenPlayers.add(player.uniqueId.toString())
    }

    fun removeHiddenPlayer(player: Player) {
        hiddenPlayers.remove(player.uniqueId.toString())
    }

    fun hasPlayer(player: Player): Boolean {
        return hiddenPlayers.contains(player.uniqueId.toString())
    }

    fun addIgnoredPlayer(player: Player) {
        ignoredPlayers.add(player.uniqueId)
    }

    fun removeIgnoredPlayer(player: Player) {
        ignoredPlayers.remove(player.uniqueId)
    }
}