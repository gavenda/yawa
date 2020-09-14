/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (C) 2020 Gavenda <gavenda@disroot.org>
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

package work.gavenda.yawa

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import work.gavenda.yawa.afk.disableAfk
import work.gavenda.yawa.afk.enableAfk
import work.gavenda.yawa.ender.disableEnder
import work.gavenda.yawa.ender.enableEnder
import work.gavenda.yawa.essentials.disableEssentials
import work.gavenda.yawa.essentials.enableEssentials
import work.gavenda.yawa.login.disableLogin
import work.gavenda.yawa.login.enableLogin
import work.gavenda.yawa.ping.disablePing
import work.gavenda.yawa.ping.enablePing
import work.gavenda.yawa.skin.disableSkin
import work.gavenda.yawa.skin.enableSkin
import work.gavenda.yawa.sleep.disableSleep
import work.gavenda.yawa.sleep.enableSleep
import work.gavenda.yawa.tablist.disableTabList
import work.gavenda.yawa.tablist.enableTabList

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
        enableEssentials()
        enablePing()
        enableSkin()
        enableAfk()
        enableSleep()
        enableTabList()
        enableLogin()
        enableEnder()
        // Register root command
        registerRootCommand()

        safeLoad = true
    }

    override fun onDisable() {
        if (!safeLoad) {
            slF4JLogger.warn("Plugin was not able to start safely, restarting your server might be best. Please check your configuration.")
            return
        }

        // Unregister root command
        unregisterRootCommand()

        // Disable features
        disableEssentials()
        disablePing()
        disableSkin()
        disableAfk()
        disableSleep()
        disableTabList()
        disableLogin()
        disableEnder()
        // Close data source
        dataSource.close()
        // Safe load flag to false, in case of reloads
        safeLoad = false
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

    fun loadConfig() {
        Config.load(config)
    }

    private fun registerRootCommand() {
        val rootCommand = YawaCommand().apply {
            val reloadCommand = YawaReloadCommand().apply {
                sub(YawaReloadConfigCommand(), "config")
            }

            sub(reloadCommand, "reload")
        }

        getCommand("yawa")?.setExecutor(rootCommand)
    }

    private fun unregisterRootCommand() {
        getCommand("yawa")?.setExecutor(null)
    }
}
