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

import club.minnced.discord.webhook.external.JDAWebhookClient
import club.minnced.discord.webhook.send.WebhookMessageBuilder
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Webhook
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Player
import work.gavenda.yawa.*
import work.gavenda.yawa.api.asAwtColor
import work.gavenda.yawa.api.placeholder.Placeholders
import work.gavenda.yawa.api.placeholder.provider.PlayerPlaceholderProvider
import work.gavenda.yawa.api.toPlainText


object DiscordFeature : PluginFeature, EventListener {
    override val disabled: Boolean
        get() = Config.Discord.Disabled

    private val emojiRegex = Regex("(<a?)?:\\w+:(\\d{18}>)?")
    private const val defaultAvatarUrl = "https://cravatar.eu/avatar/shirobiru/50.png"
    private val playerListener = PlayerListener()
    private val chatListener = ChatListener()
    private val slashCommandListener = SlashCommandListener()
    private lateinit var jda: JDA
    private lateinit var guild: Guild
    private lateinit var textChannel: TextChannel
    private lateinit var webhook: Webhook

    override fun registerHooks() {
        jda = JDABuilder.createDefault(Config.Discord.Token)
            .setMemberCachePolicy(MemberCachePolicy.ONLINE)
            .setAutoReconnect(true)
            .setEnableShutdownHook(false)
            .enableIntents(
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                GatewayIntent.MESSAGE_CONTENT
            )
            .build()
            .awaitReady()

        guild = jda.getGuildById(Config.Discord.GuildId) ?: error("Unknown guild")
        textChannel = guild.getTextChannelById(Config.Discord.GuildChannel) ?: error("Unknown text channel")

        // Check existing webhooks
        guild.retrieveWebhooks().queue { webhooks ->
            val foundWebhook = webhooks.firstOrNull { it.name == Config.Discord.GuildWebhook }
            if (foundWebhook != null) {
                webhook = foundWebhook
            } else {
                textChannel.createWebhook(Config.Discord.GuildWebhook).queue {
                    webhook = it
                }
            }
        }

        // Register slash commands
        jda.updateCommands().addCommands(
            Commands.slash("online", "Show online players."),
            Commands.slash("server", "Show server information.")
        ).queue()

        // Update status
        jda.presence.activity = Activity.playing("Minecraft")
    }

    fun sendMessage(player: Player, component: Component) {
        sendMessage(player, component.toPlainText())
    }

    fun sendMessage(player: Player, message: String) {
        if (disabled) return

        var messageProcessed = message
        val replacements = mutableMapOf<String, String>()
        emojiRegex.findAll(message).forEach {
            val emoteName = it.value.removeSurrounding(":")
            val emote = guild
                .getEmojisByName(emoteName, true)
                .firstOrNull()

            if (emote != null) {
                replacements.putIfAbsent(it.value, emote.asMention)
            }
        }

        replacements.forEach { (match, mention) ->
            messageProcessed = messageProcessed.replace(match, mention, true)
        }

        logger.info("Sending $messageProcessed")

        JDAWebhookClient.from(webhook).use { client ->
            client.send(
                WebhookMessageBuilder()
                    .setUsername(player.displayName().toPlainText())
                    .setAvatarUrl(player.avatarUrl)
                    .setContent(messageProcessed)
                    .build()
            )
        }
    }

    fun sendAlert(component: Component, avatarUrl: String = defaultAvatarUrl, color: TextColor = NamedTextColor.BLACK) {
        sendAlert(component.toPlainText(), avatarUrl, color)
    }

    fun sendAlert(alert: String, avatarUrl: String = defaultAvatarUrl, color: TextColor = NamedTextColor.BLACK) {
        if (disabled) return

        val embed = EmbedBuilder()
            .setAuthor(alert, null, avatarUrl)
            .setColor(color.asAwtColor())
            .build()

        textChannel.sendMessageEmbeds(embed).queue()
    }

    override fun onEvent(event: GenericEvent) {
        when (event) {
            is MessageReceivedEvent -> {
                if (event.author.isBot) return
                if (event.channel.idLong != Config.Discord.GuildChannel) return
                if (event.message.contentRaw.isBlank() && event.message.attachments.isEmpty()) return

                val messageRaw = if (event.message.contentRaw.isBlank()) {
                    Config.Discord.MessageFormat + event.message.attachments.first().url
                } else {
                    Config.Discord.MessageFormat + event.message.contentDisplay
                }

                val message = Placeholders.noContext()
                    .parse(
                        messageRaw, mapOf(
                            PlayerPlaceholderProvider.NAME to event.member?.effectiveName
                        )
                    )

                server.consoleSender.sendMessage(message)
                server.onlinePlayers.forEach {
                    it.sendMessage(message)
                }
            }
        }
    }

    override fun unregisterHooks() {
        jda.shutdown()
    }

    override fun registerEventListeners() {
        jda.addEventListener(this, slashCommandListener)
        pluginManager.registerEvents(chatListener)
        pluginManager.registerEvents(playerListener)
    }

    override fun unregisterEventListeners() {
        jda.removeEventListener(this, slashCommandListener)
        pluginManager.unregisterEvents(chatListener)
        pluginManager.unregisterEvents(playerListener)
    }
}