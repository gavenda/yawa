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
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.permission.Permission
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import work.gavenda.yawa.afk.AfkFeature
import work.gavenda.yawa.api.compat.PluginEnvironment
import work.gavenda.yawa.api.compat.pluginEnvironment
import work.gavenda.yawa.chat.ChatFeature
import work.gavenda.yawa.chunk.ChunkFeature
import work.gavenda.yawa.discord.DiscordFeature
import work.gavenda.yawa.ender.EnderFeature
import work.gavenda.yawa.essentials.EssentialsFeature
import work.gavenda.yawa.hiddenarmor.HiddenArmorFeature
import work.gavenda.yawa.login.LoginFeature
import work.gavenda.yawa.notify.NotifyFeature
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
        DiscordFeature.enable()
        EnderFeature.enable()
        EssentialsFeature.enable()
        HiddenArmorFeature.enable()
        LoginFeature.enable()
        NotifyFeature.enable()
        PlayerHeadFeature.enable()
        PermissionFeature.enable()
        SitFeature.enable()
        SkinFeature.enable()
        SleepFeature.enable()
        TabListFeature.enable()

        // Register root command
        registerRootCommand()
        registerVault()

        safeLoad = true
    }

    override fun onDisable() {
        if (!safeLoad) {
            work.gavenda.yawa.logger.warn("Plugin was not able to start safely, restarting your server might be best. Please check your configuration.")
            return
        }

        // Unregister root command
        unregisterRootCommand()

        // Disable features
        AfkFeature.disable()
        ChatFeature.disable()
        ChunkFeature.disable()
        DiscordFeature.disable()
        EnderFeature.disable()
        EssentialsFeature.disable()
        HiddenArmorFeature.disable()
        LoginFeature.disable()
        NotifyFeature.disable()
        PingFeature.disable()
        PlayerHeadFeature.disable()
        PermissionFeature.disable()
        SitFeature.disable()
        SkinFeature.disable()
        SleepFeature.disable()
        TabListFeature.disable()
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

    fun loadConfig() {
        Config.load(config)
    }

    private fun registerVault() {
        VaultUtil.Permission = server.servicesManager.getRegistration(Permission::class.java)?.provider
        VaultUtil.Chat = server.servicesManager.getRegistration(Chat::class.java)?.provider
        VaultUtil.Economy = server.servicesManager.getRegistration(Economy::class.java)?.provider
    }

    private fun registerRootCommand() {
        if (pluginEnvironment == PluginEnvironment.PAPER) {
            server.pluginManager.registerEvents(rootCommand, this)
        }
        getCommand(Command.ROOT)?.setExecutor(rootCommand)
    }

    private fun unregisterRootCommand() {
        getCommand(Command.ROOT)?.setExecutor(null)
        if (pluginEnvironment == PluginEnvironment.PAPER) {
            HandlerList.unregisterAll(rootCommand)
        }
    }
}
