package work.gavenda.yawa

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
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

    private var safeLoad = false
    private lateinit var dataSource: HikariDataSource

    companion object {
        lateinit var Instance: Plugin
    }

    override fun onEnable() {
        // Instance
        Instance = this
        // Load configuration
        saveDefaultConfig()
        loadConfig()
        // Init data source
        initDataSource()
        // Enable features
        enablePing()
        enableSkin()
        enableAfk()
        enableSleep()

        safeLoad = true
    }

    override fun onDisable() {
        if(!safeLoad) {
            slF4JLogger.warn("Plugin was not able to start safely, restarting your server might be best.")
            return
        }

        // Disable features
        disablePing()
        disableSkin()
        disableAfk()
        disableSleep()
        // Close data source
        dataSource.close()
    }

    private fun initDataSource() {
        val config = HikariConfig()

        config.jdbcUrl = Config.Database.JdbcUrl
        config.username = Config.Database.Username
        config.password = Config.Database.Password

        dataSource = HikariDataSource(config)

        // Use data source in Exposed
        Database.connect(dataSource)
    }

    private fun loadConfig() {
        Config.load(config)
    }
}
