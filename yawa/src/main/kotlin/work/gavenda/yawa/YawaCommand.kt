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
import work.gavenda.yawa.ender.EnderFeature
import work.gavenda.yawa.essentials.disableEssentials
import work.gavenda.yawa.essentials.enableEssentials
import work.gavenda.yawa.login.disableLogin
import work.gavenda.yawa.login.enableLogin
import work.gavenda.yawa.permission.disablePermission
import work.gavenda.yawa.permission.enablePermission
import work.gavenda.yawa.ping.disablePing
import work.gavenda.yawa.ping.enablePing
import work.gavenda.yawa.sit.disableSit
import work.gavenda.yawa.sit.enableSit
import work.gavenda.yawa.skin.disableSkin
import work.gavenda.yawa.skin.enableSkin
import work.gavenda.yawa.sleep.disableSleep
import work.gavenda.yawa.sleep.enableSleep
import work.gavenda.yawa.tablist.disableTabList
import work.gavenda.yawa.tablist.enableTabList

// Switches
const val FEATURE_SWITCH_ENABLE = "enable"
const val FEATURE_SWITCH_DISABLE = "disable"

private val featureEnableMap = mapOf(
    Feature.AFK to { AfkFeature.enable() },
    Feature.ENDER to { EnderFeature.enable() },
    Feature.ESSENTIALS to { Yawa.Instance.enableEssentials() },
    Feature.LOGIN to { Yawa.Instance.enableLogin() },
    Feature.PING to { Yawa.Instance.enablePing() },
    Feature.PERMISSION to { Yawa.Instance.enablePermission() },
    Feature.SIT to { Yawa.Instance.enableSit() },
    Feature.SKIN to { Yawa.Instance.enableSkin() },
    Feature.SLEEP to { Yawa.Instance.enableSleep() },
    Feature.TABLIST to { Yawa.Instance.enableTabList() },
    Feature.KEEP_ALIVE to { Yawa.Instance.adjustKeepAliveTimeout() },
)

private val featureDisableMap = mapOf(
    Feature.AFK to { AfkFeature.disable() },
    Feature.ENDER to { EnderFeature.disable() },
    Feature.ESSENTIALS to { Yawa.Instance.disableEssentials() },
    Feature.LOGIN to { Yawa.Instance.disableLogin() },
    Feature.PING to { Yawa.Instance.disablePing() },
    Feature.PERMISSION to { Yawa.Instance.disablePermission() },
    Feature.SIT to { Yawa.Instance.disableSit() },
    Feature.SKIN to { Yawa.Instance.disableSkin(true) },
    Feature.SLEEP to { Yawa.Instance.disableSleep() },
    Feature.TABLIST to { Yawa.Instance.disableTabList() },
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
                sender.sendMessage("&ePlease provide if enable or disable")
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