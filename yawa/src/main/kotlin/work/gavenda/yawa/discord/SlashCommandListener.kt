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

package work.gavenda.yawa.discord

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import work.gavenda.yawa.api.compat.PLUGIN_ENVIRONMENT
import work.gavenda.yawa.api.compat.PluginEnvironment
import work.gavenda.yawa.logger
import work.gavenda.yawa.server

class SlashCommandListener : ListenerAdapter() {

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        event.deferReply(true).queue()

        when (event.name) {
            "online" -> showOnline(event)
            "server" -> showServer(event)
            else -> logger.info("Unknown slash command: ${event.name}")
        }
    }

    private fun showOnline(event: SlashCommandInteractionEvent) {
        val onlinePlayers = if (server.onlinePlayers.size > 0) {
            server.onlinePlayers.joinToString(separator = "\n") { "- ${it.name}" }
        } else {
            "There are no online players."
        }
        val embed = EmbedBuilder()
            .setTitle("Online Players")
            .setDescription(onlinePlayers)
            .setColor(EMBED_COLOR)
            .setFooter("Note: Beware of Sequenzi, top tier bogus.")
            .build()

        event.hook.sendMessageEmbeds(embed).queue()
    }

    private fun showServer(event: SlashCommandInteractionEvent) {
        val embed = EmbedBuilder()
            .setTitle("Server Information")
            .addField("Server", server.version, false)
            .addField("Online Players", "${server.onlinePlayers.size} / ${server.maxPlayers}", false)

        if (PLUGIN_ENVIRONMENT == PluginEnvironment.PAPER) {
            embed.addField("Version", server.minecraftVersion, false)
            embed.addField("Average Tick Time", "${server.averageTickTime}ms", true)
            embed.addField("Ticks Per Second", "${server.tps[0]} (1m), ${server.tps[1]} (5m), ${server.tps[2]} (15m)", true)
        }

        event.hook.sendMessageEmbeds(embed.build()).queue()
    }
}