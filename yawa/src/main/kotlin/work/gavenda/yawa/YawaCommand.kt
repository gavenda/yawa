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

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import work.gavenda.yawa.afk.AfkFeature
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.HelpList
import work.gavenda.yawa.chat.ChatFeature
import work.gavenda.yawa.ender.EnderFeature
import work.gavenda.yawa.essentials.EssentialsFeature
import work.gavenda.yawa.login.LoginFeature
import work.gavenda.yawa.permission.PermissionFeature
import work.gavenda.yawa.ping.PingFeature
import work.gavenda.yawa.playerhead.PlayerHeadFeature
import work.gavenda.yawa.sit.SitFeature
import work.gavenda.yawa.skin.SkinFeature
import work.gavenda.yawa.sleep.SleepFeature
import work.gavenda.yawa.tablist.TabListFeature

// Switches
const val FEATURE_SWITCH_ENABLE = "enable"
const val FEATURE_SWITCH_DISABLE = "disable"

private val featureEnableMap = mapOf(
    Feature.AFK to { AfkFeature.enable() },
    Feature.CHAT to { ChatFeature.enable() },
    Feature.ENDER to { EnderFeature.enable() },
    Feature.ESSENTIALS to { EssentialsFeature.enable() },
    Feature.LOGIN to { LoginFeature.enable() },
    Feature.PING to { PingFeature.enable() },
    Feature.PLAYER_HEAD to { PlayerHeadFeature.enable() },
    Feature.PERMISSION to { PermissionFeature.enable() },
    Feature.SIT to { SitFeature.enable() },
    Feature.SKIN to { SkinFeature.enable() },
    Feature.SLEEP to { SleepFeature.enable() },
    Feature.TABLIST to { TabListFeature.enable() },
    Feature.KEEP_ALIVE to { Yawa.Instance.adjustKeepAliveTimeout() },
)

private val featureDisableMap = mapOf(
    Feature.AFK to { AfkFeature.disable() },
    Feature.CHAT to { ChatFeature.disable() },
    Feature.ENDER to { EnderFeature.disable() },
    Feature.ESSENTIALS to { EssentialsFeature.disable() },
    Feature.LOGIN to { LoginFeature.disable() },
    Feature.PING to { PingFeature.disable() },
    Feature.PLAYER_HEAD to { PlayerHeadFeature.disable() },
    Feature.PERMISSION to { PermissionFeature.disable() },
    Feature.SIT to { SitFeature.disable() },
    Feature.SKIN to { SkinFeature.disable() },
    Feature.SLEEP to { SleepFeature.disable() },
    Feature.TABLIST to { TabListFeature.disable() },
    Feature.KEEP_ALIVE to { Yawa.Instance.resetKeepAliveTimeout() },
)

private val featureSwitch = listOf(FEATURE_SWITCH_ENABLE, FEATURE_SWITCH_DISABLE)
private val yawaCommands = listOf("yawa", "yawa:yawa")

/**
 * Plugin main command.
 */
class YawaCommand : Command(commands = yawaCommands) {

    override fun execute(sender: CommandSender, args: List<String>) {
        HelpList()
            .command("yawa reload", listOf("<config>"), "Reloads the plugin", Permission.RELOAD)
            .command(
                "yawa feature",
                listOf("<feature>", "<enable|disable>"),
                "Enable or disable a feature",
                Permission.FEATURE
            )
            .generateMessages(sender)
            .forEach(sender::sendMessage)
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return subCommandKeys.toList()
    }

}

/**
 * Enable or disable a feature.
 */
class YawaFeatureCommand : Command(Permission.FEATURE) {

    override fun execute(sender: CommandSender, args: List<String>) {
        if (args.size == 2) {
            val feature = args[0]
            val switch = args[1]

            if (featureSwitch.contains(switch).not()) {
                sender.sendMessageUsingKey(Message.FeatureValueInvalid)
                return
            }
            if (switch == FEATURE_SWITCH_ENABLE) {
                val enableFeature = featureEnableMap[feature] ?: return

                // Enable in config first
                Config.set("$feature.disabled", false)

                enableFeature()

                sender.sendMessageUsingKey(Message.FeatureSetEnabled)
                logger.info("Feature '$feature' has been enabled")
            }
            if (switch == FEATURE_SWITCH_DISABLE) {
                val disableFeature = featureDisableMap[feature] ?: return

                disableFeature()

                sender.sendMessageUsingKey(Message.FeatureSetDisabled)
                logger.info("Feature '$feature' has been disabled")

                // Disable in config last
                Config.set("$feature.disabled", true)
            }

            if (sender is Player) {
                sender.updateCommands()
            }
        }
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return when (args.size) {
            1 -> featureEnableMap.keys.toList()
            2 -> featureSwitch
            else -> emptyList()
        }
    }
}

/**
 * Reloads the plugin.
 */
class YawaReloadCommand : Command(Permission.RELOAD) {
    override fun execute(sender: CommandSender, args: List<String>) {
        if (args.isEmpty()) {
            Yawa.Instance.onDisable()
            Yawa.Instance.reloadConfig()
            Yawa.Instance.loadConfig()
            Yawa.Instance.onEnable()

            sender.sendMessageUsingKey(Message.PluginReload)
            return
        }

        if (args[0] == "config") {
            Yawa.Instance.reloadConfig()
            Yawa.Instance.loadConfig()

            sender.sendMessageUsingKey(Message.PluginReloadConfig)
        }
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return when (args.size) {
            1 -> listOf("config")
            else -> emptyList()
        }
    }
}