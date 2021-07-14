/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 *  Copyright (C) 2021 Gavenda <gavenda@disroot.org>
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
 *
 */

package work.gavenda.yawa

import com.comphenix.protocol.utility.MinecraftReflection
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import work.gavenda.yawa.afk.AfkFeature
import work.gavenda.yawa.chat.ChatFeature
import work.gavenda.yawa.chunk.ChunkFeature
import work.gavenda.yawa.ender.EnderFeature
import work.gavenda.yawa.essentials.EssentialsFeature
import work.gavenda.yawa.imgup.ImageUploadFeature
import work.gavenda.yawa.login.LoginFeature
import work.gavenda.yawa.permission.PermissionFeature
import work.gavenda.yawa.ping.PingFeature
import work.gavenda.yawa.playerhead.PlayerHeadFeature
import work.gavenda.yawa.sit.SitFeature
import work.gavenda.yawa.skin.SkinFeature
import work.gavenda.yawa.sleep.SleepFeature
import work.gavenda.yawa.tablist.TabListFeature
import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * Yawa plugin entry point.
 */
class Yawa : JavaPlugin() {

    private val startupListener = StartupListener()
    private var safeLoad = false
    private lateinit var dataSource: HikariDataSource
    private val rootCommand = YawaCommand().apply {
        sub(YawaReloadCommand(), "reload")
        sub(YawaFeatureCommand(), "feature")
    }

    companion object {
        lateinit var Instance: Yawa
    }

    override fun onEnable() {
        // Instance
        Instance = this
        // Load configuration
        saveDefaultConfig()
        loadConfig()
        // Init data source
        initDataSource()
        // Listen to POST-server startup, required for some features
        server.pluginManager.registerEvents(startupListener, this)
        // Enable features
        AfkFeature.enable()
        ChatFeature.enable()
        EnderFeature.enable()
        EssentialsFeature.enable()
        ImageUploadFeature.enable()
        LoginFeature.enable()
        PlayerHeadFeature.enable()
        PermissionFeature.enable()
        SitFeature.enable()
        SkinFeature.enable()
        SleepFeature.enable()
        TabListFeature.enable()

        // Register root command
        registerRootCommand()
        adjustKeepAliveTimeout()

        safeLoad = true
    }

    override fun onDisable() {
        if (!safeLoad) {
            work.gavenda.yawa.logger.warn("Plugin was not able to start safely, restarting your server might be best. Please check your configuration.")
            return
        }

        // Reset keep alive timeout
        resetKeepAliveTimeout()
        // Unregister root command
        unregisterRootCommand()

        // Disable features
        AfkFeature.disable()
        ChatFeature.disable()
        EnderFeature.disable()
        EssentialsFeature.disable()
        ImageUploadFeature.disable()
        LoginFeature.disable()
        PingFeature.disable()
        PlayerHeadFeature.disable()
        PermissionFeature.disable()
        SitFeature.disable()
        SkinFeature.disable()
        SleepFeature.disable()
        TabListFeature.disable()
        ChunkFeature.disable()
        // Unregister startup listener
        HandlerList.unregisterAll(startupListener)
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

    fun resetKeepAliveTimeout() {
        if (Config.KeepAlive.Disabled) return

        // This is a paper plugin, resetting should also be paper-based
        work.gavenda.yawa.logger.warn("Resetting keep alive timeout")

        val longStr = System.getProperty("paper.playerconnection.keepalive") ?: "30"
        val long = longStr.toLong()

        adjustKeepAliveTimeout(long * 1000)
    }

    fun adjustKeepAliveTimeout(timeout: Long = Config.KeepAlive.Timeout * 1000) {
        if (Config.KeepAlive.Disabled) return

        work.gavenda.yawa.logger.warn("Adjusting keep alive timeout to $timeout ms")

        val nmsPlayerConnection = MinecraftReflection.getPlayerConnectionClass()
        val field = nmsPlayerConnection.getDeclaredField("KEEPALIVE_LIMIT").apply {
            isAccessible = true
        }

        // Change final to non-final
        Field::class.java.getDeclaredField("modifiers").apply {
            isAccessible = true
            setInt(field, field.modifiers and Modifier.FINAL.inv())
        }

        field.setLong(null, timeout)
    }

    fun loadConfig() {
        Config.load(config)
    }

    private fun registerRootCommand() {
        server.pluginManager.registerEvents(rootCommand, this)
        getCommand(Command.ROOT)?.setExecutor(rootCommand)
    }

    private fun unregisterRootCommand() {
        getCommand(Command.ROOT)?.setExecutor(null)
        HandlerList.unregisterAll(rootCommand)
    }
}
