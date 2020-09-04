package work.gavenda.yawa

import org.bukkit.plugin.java.JavaPlugin
import work.gavenda.yawa.afk.disableAfk
import work.gavenda.yawa.afk.enableAfk
import work.gavenda.yawa.ping.disablePing
import work.gavenda.yawa.ping.enablePing
import work.gavenda.yawa.skin.disableSkin
import work.gavenda.yawa.skin.enableSkin
import work.gavenda.yawa.sleep.disableSleep
import work.gavenda.yawa.sleep.enableSleep

/**
 * Yawa plugin entry point.
 */
class Plugin : JavaPlugin() {

    companion object {
        lateinit var Instance: Plugin
    }

    override fun onEnable() {
        // Instance
        Instance = this
        // Load configuration
        saveDefaultConfig()
        loadConfig()

        enablePing()
        enableSkin()
        enableAfk()
        enableSleep()
    }

    override fun onDisable() {
        disablePing()
        disableSkin()
        disableAfk()
        disableSleep()
    }

    private fun loadConfig() {
        Config.load(config)
    }
}
