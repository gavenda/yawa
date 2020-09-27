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

package work.gavenda.yawa.chat

import github.scarsz.discordsrv.DiscordSRV
import work.gavenda.yawa.*

object ChatFeature : PluginFeature {
    override val isDisabled get() = Config.Chat.Disabled

    private val isDiscordSRVEnabled = pluginManager.getPlugin("DiscordSRV") != null

    private val whisperCommand = WhisperCommand()
    private val replyCommand = ReplyCommand()
    private val chatListener = ChatListener()

    private lateinit var discordSRVListener: DiscordSRVListener

    override fun enableCommands() {
        plugin.getCommand(Command.WHISPER)?.setExecutor(whisperCommand)
        plugin.getCommand(Command.REPLY)?.setExecutor(replyCommand)
    }

    override fun disableCommands() {
        plugin.getCommand(Command.WHISPER)?.setExecutor(DisabledCommand)
        plugin.getCommand(Command.REPLY)?.setExecutor(DisabledCommand)
    }

    override fun registerHooks() {
        if (isDiscordSRVEnabled) {
            logger.info("DiscordSRV detected, attaching message post processor")

            discordSRVListener = DiscordSRVListener()
            DiscordSRV.api.subscribe(discordSRVListener)
        }
    }

    override fun unregisterHooks() {
        if (isDiscordSRVEnabled) {
            logger.info("DiscordSRV detected, detaching message post processor")

            DiscordSRV.api.unsubscribe(discordSRVListener)
        }
    }

    override fun registerEventListeners() {
        pluginManager.registerEvents(chatListener)
    }

    override fun unregisterEventListeners() {
        pluginManager.unregisterEvents(chatListener)
    }
}