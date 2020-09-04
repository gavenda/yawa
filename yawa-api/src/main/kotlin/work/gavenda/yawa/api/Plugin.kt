package work.gavenda.yawa.api

import org.bukkit.plugin.java.JavaPlugin
import work.gavenda.yawa.api.providers.PlayerPlaceholderProvider
import work.gavenda.yawa.api.providers.WorldPlaceholderProvider

/**
 * Yawa API plugin entry point.
 */
class Plugin : JavaPlugin() {

    companion object {
        lateinit var Instance: Plugin
    }

    override fun onEnable() {
        Instance = this

        // Register placeholders
        Placeholder.register(PlayerPlaceholderProvider())
        Placeholder.register(WorldPlaceholderProvider())
    }

    override fun onDisable() {
        Placeholder.clear()
    }
}
