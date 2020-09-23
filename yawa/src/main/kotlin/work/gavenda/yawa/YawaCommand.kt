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
import work.gavenda.yawa.afk.disableAfk
import work.gavenda.yawa.afk.enableAfk
import work.gavenda.yawa.api.Command
import work.gavenda.yawa.api.HelpList
import work.gavenda.yawa.api.translateColorCodes
import work.gavenda.yawa.ender.disableEnder
import work.gavenda.yawa.ender.enableEnder
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

// Features
const val FEATURE_AFK = "afk"
const val FEATURE_ESSENTIALS = "essentials"
const val FEATURE_ENDER = "ender"
const val FEATURE_LOGIN = "login"
const val FEATURE_PERMISSION = "permission"
const val FEATURE_PING = "ping"
const val FEATURE_SIT = "sit"
const val FEATURE_SKIN = "skin"
const val FEATURE_SLEEP = "sleep"
const val FEATURE_TABLIST = "tab-list"
const val FEATURE_KEEP_ALIVE = "keep-alive"

private val featureEnableMap = mapOf(
    FEATURE_AFK to { Plugin.Instance.enableAfk() },
    FEATURE_ENDER to { Plugin.Instance.enableEnder() },
    FEATURE_ESSENTIALS to { Plugin.Instance.enableEssentials() },
    FEATURE_LOGIN to { Plugin.Instance.enableLogin() },
    FEATURE_PING to { Plugin.Instance.enablePing() },
    FEATURE_PERMISSION to { Plugin.Instance.enablePermission() },
    FEATURE_SIT to { Plugin.Instance.enableSit() },
    FEATURE_SKIN to { Plugin.Instance.enableSkin() },
    FEATURE_SLEEP to { Plugin.Instance.enableSleep() },
    FEATURE_TABLIST to { Plugin.Instance.enableTabList() },
    FEATURE_KEEP_ALIVE to { Plugin.Instance.adjustKeepAliveTimeout() },
)

private val featureDisableMap = mapOf(
    FEATURE_AFK to { Plugin.Instance.disableAfk(true) },
    FEATURE_ENDER to { Plugin.Instance.disableEnder() },
    FEATURE_ESSENTIALS to { Plugin.Instance.disableEssentials() },
    FEATURE_LOGIN to { Plugin.Instance.disableLogin() },
    FEATURE_PING to { Plugin.Instance.disablePing() },
    FEATURE_PERMISSION to { Plugin.Instance.disablePermission() },
    FEATURE_SIT to { Plugin.Instance.disableSit() },
    FEATURE_SKIN to { Plugin.Instance.disableSkin(true) },
    FEATURE_SLEEP to { Plugin.Instance.disableSleep() },
    FEATURE_TABLIST to { Plugin.Instance.disableTabList() },
    FEATURE_KEEP_ALIVE to { Plugin.Instance.resetKeepAliveTimeout() },
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
 * Enable a feature.
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
                // Enable in config first
                Config.set("$feature.disabled", false)

                featureEnableMap[feature]?.invoke().also {
                    sender.sendMessage(Config.Messages.FeatureSetEnabled.translateColorCodes())
                    logger.info("Feature '$feature' has been enabled")
                }
            }
            if (switch == FEATURE_SWITCH_DISABLE) {
                featureDisableMap[feature]?.invoke().also {
                    sender.sendMessage(Config.Messages.FeatureSetDisabled.translateColorCodes())
                    logger.info("Feature '$feature' has been disabled")

                    // Disable in config last
                    Config.set("$feature.disabled", true)
                }
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
            Plugin.Instance.onDisable()
            Plugin.Instance.reloadConfig()
            Plugin.Instance.loadConfig()
            Plugin.Instance.onEnable()
            sender.sendMessage(Config.Messages.PluginReload.translateColorCodes())
            return
        }

        if (args[0] == "config") {
            Plugin.Instance.reloadConfig()
            Plugin.Instance.loadConfig()

            sender.sendMessage(Config.Messages.PluginReloadConfig.translateColorCodes())
        }
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return when (args.size) {
            1 -> listOf("config")
            else -> emptyList()
        }
    }
}