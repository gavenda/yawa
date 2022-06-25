package work.gavenda.yawa.hiddenarmor

import org.bukkit.GameMode
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

    fun shouldNotHideSelf(player: Player): Boolean {
        return !hasPlayer(player) ||
                player.isInvisible ||
                (player.gameMode == GameMode.CREATIVE) ||
                ignoredPlayers.contains(player.uniqueId)
    }

    fun shouldNotHideOthers(player: Player): Boolean {
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