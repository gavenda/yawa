package work.gavenda.yawa.discord

import club.minnced.discord.webhook.external.JDAWebhookClient
import club.minnced.discord.webhook.send.WebhookMessageBuilder
import com.neovisionaries.ws.client.DualStackMode
import com.neovisionaries.ws.client.WebSocketFactory
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.Webhook
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Player
import work.gavenda.yawa.*
import work.gavenda.yawa.api.compat.displayNameCompat
import work.gavenda.yawa.api.compat.sendMessageCompat
import work.gavenda.yawa.api.placeholder.Placeholders
import work.gavenda.yawa.api.toPlainText

object DiscordFeature : PluginFeature, EventListener {
    override val disabled: Boolean
        get() = Config.Discord.Disabled

    private val emojiRegex = Regex("(<a?)?:\\w+:(\\d{18}>)?")
    private const val defaultAvatarUrl = "https://cravatar.eu/avatar/shirobiru/50.png"
    private val playerListener = PlayerListener()
    private var topicTaskId = -1
    private lateinit var jda: JDA
    private lateinit var guild: Guild
    private lateinit var textChannel: TextChannel
    private lateinit var webhook: Webhook

    override fun registerHooks() {
        jda = JDABuilder.createDefault(Config.Discord.Token)
            .setWebsocketFactory(
                WebSocketFactory().setDualStackMode(DualStackMode.IPV4_ONLY)
            )
            .setMemberCachePolicy(MemberCachePolicy.ONLINE)
            .setAutoReconnect(true)
            .setEnableShutdownHook(true)
            .enableIntents(
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_EMOJIS
            )
            .build()
            .awaitReady()

        guild = jda.getGuildById(Config.Discord.GuildId)
            ?: throw IllegalStateException("Unknown guild")
        textChannel = guild.getTextChannelById(Config.Discord.GuildChannel)
            ?: throw IllegalStateException("Unknown text channel")

        topicTaskId = scheduler.runTaskTimerAsynchronously(plugin, { ->
            textChannel.manager
                .setTopic("${server.onlinePlayers.size} / ${server.maxPlayers} online")
                .queue()
        }, 0, 20 * 120).taskId

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
    }

    fun sendMessage(player: Player, message: String) {
        if (disabled) return

        var messageProcessed = message
        val replacements = mutableMapOf<String, String>()
        emojiRegex.findAll(message).forEach {
            val emoteName = it.value.removeSurrounding(":")
            val emote = guild
                .getEmotesByName(emoteName, true)
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
                    .setUsername(player.displayNameCompat.toPlainText())
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

                val messageRaw = Config.Discord.MessageFormat + event.message.contentDisplay
                val message = Placeholders.noContext()
                    .parse(
                        messageRaw, mapOf(
                            "player-name" to event.member?.effectiveName
                        )
                    )

                server.onlinePlayers.forEach {
                    it.sendMessageCompat(message)
                }
            }
        }
    }

    override fun unregisterHooks() {
        jda.shutdown()
        scheduler.cancelTask(topicTaskId)
        textChannel.manager
            .setTopic("Server offline")
            .queue()
    }

    override fun registerEventListeners() {
        jda.addEventListener(this)
        pluginManager.registerEvents(playerListener)
    }

    override fun unregisterEventListeners() {
        jda.removeEventListener(this)
        pluginManager.unregisterEvents(playerListener)
    }
}