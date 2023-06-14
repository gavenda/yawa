/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (c) 2022 Gavenda <gavenda@disroot.org>
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

// Switches
const val FEATURE_SWITCH_ENABLE = "enable"
const val FEATURE_SWITCH_DISABLE = "disable"

private val featureEnableMap = mapOf(
    Feature.AFK to { AfkFeature.enable() },
    Feature.CHAT to { ChatFeature.enable() },
    Feature.CHUNK to { ChunkFeature.enable() },
    Feature.DISCORD to { DiscordFeature.enable() },
    Feature.ENDER to { EnderFeature.enable() },
    Feature.ESSENTIALS to { EssentialsFeature.enable() },
    Feature.HIDDEN_ARMOR to { HiddenArmorFeature.enable() },
    Feature.LOGIN to { LoginFeature.enable() },
    Feature.NOTIFY to { NotifyFeature.enable() },
    Feature.PERMISSION to { PermissionFeature.enable() },
    Feature.PING to { PingFeature.enable() },
    Feature.PLAYER_HEAD to { PlayerHeadFeature.enable() },
    Feature.SIT to { SitFeature.enable() },
    Feature.SKIN to { SkinFeature.enable() },
    Feature.SLEEP to { SleepFeature.enable() },
    Feature.TAB_LIST to { TabListFeature.enable() }
)

private val featureDisableMap = mapOf(
    Feature.AFK to { AfkFeature.disable() },
    Feature.CHAT to { ChatFeature.disable() },
    Feature.CHUNK to { ChunkFeature.disable() },
    Feature.DISCORD to { DiscordFeature.disable() },
    Feature.ENDER to { EnderFeature.disable() },
    Feature.ESSENTIALS to { EssentialsFeature.disable() },
    Feature.HIDDEN_ARMOR to { HiddenArmorFeature.disable() },
    Feature.LOGIN to { LoginFeature.disable() },
    Feature.NOTIFY to { NotifyFeature.disable() },
    Feature.PERMISSION to { PermissionFeature.disable() },
    Feature.PING to { PingFeature.disable() },
    Feature.PLAYER_HEAD to { PlayerHeadFeature.disable() },
    Feature.SIT to { SitFeature.disable() },
    Feature.SKIN to { SkinFeature.disable() },
    Feature.SLEEP to { SleepFeature.disable() },
    Feature.TAB_LIST to { TabListFeature.disable() }
)

private val FEATURE_SWITCH = listOf(FEATURE_SWITCH_ENABLE, FEATURE_SWITCH_DISABLE)

/**
 * Plugin main command.
 */
class RootCommand : Command() {
    override val commands = listOf("yawa")
    override fun execute(sender: CommandSender, args: List<String>) {
        HelpList()
            .command("yawa reload", listOf("<config>"), "Reloads the plugin", Permission.RELOAD)
            .command(
                "yawa feature",
                listOf("<feature>", "<enable|disable>"),
                "Enable or disable a feature",
                Permission.FEATURE
            )
            .generate(sender)
            .forEach { sender.sendMessage(it) }
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return subCommandKeys.toList()
    }

}

/**
 * Enable or disable a feature.
 */
class FeatureCommand : Command() {
    override val permission = Permission.FEATURE

    override fun execute(sender: CommandSender, args: List<String>) {
        if (args.size != 2) return

        val feature = args[0]
        val switch = args[1]

        if (FEATURE_SWITCH.contains(switch).not()) {
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

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return when (args.size) {
            1 -> featureEnableMap.keys.toList()
            2 -> FEATURE_SWITCH
            else -> emptyList()
        }
    }
}

/**
 * Reloads the plugin.
 */
class ReloadCommand : Command() {
    override val permission = Permission.RELOAD
    override fun execute(sender: CommandSender, args: List<String>) {
        when (args.size) {
            1 -> {
                if (args[0] == "config") {
                    plugin.reloadConfig()
                    plugin.loadConfig()

                    sender.sendMessageUsingKey(Message.PluginReloadConfig)
                }
            }

            else -> {
                plugin.onDisable()
                plugin.reloadConfig()
                plugin.loadConfig()
                plugin.onEnable()

                sender.sendMessageUsingKey(Message.PluginReload)
            }
        }
    }

    override fun onTab(sender: CommandSender, args: List<String>): List<String> {
        return when (args.size) {
            1 -> listOf("config")
            else -> emptyList()
        }
    }
}