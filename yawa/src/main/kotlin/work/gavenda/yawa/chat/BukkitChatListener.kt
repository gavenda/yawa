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

package work.gavenda.yawa.chat

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import work.gavenda.yawa.Config
import work.gavenda.yawa.api.placeholder.Placeholders
import work.gavenda.yawa.api.toLegacyText
import work.gavenda.yawa.login.isVerified

class BukkitChatListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @Suppress("DEPRECATION")
    fun onPlayerChat(e: org.bukkit.event.player.AsyncPlayerChatEvent) {
        handleChat(e)
    }

    @Suppress("DEPRECATION")
    fun handleChat(e: org.bukkit.event.player.AsyncPlayerChatEvent) {
        val chatFormattedComponent = Placeholders
            .withContext(e.player)
            .parse(e.message)

        val replacedFormat = if (e.player.isVerified) {
            Config.Chat.FormatMessage.replace("<player-name>", "<gold>%1\$s</gold>")
        } else {
            Config.Chat.FormatMessage.replace("<player-name>", "%1\$s")
        }

        e.format = Placeholders
            .withContext(e.player)
            .parse(replacedFormat)
            .toLegacyText()
            .plus("%2\$s")
        e.message = chatFormattedComponent.toLegacyText()
    }

}