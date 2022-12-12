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

import org.bukkit.configuration.file.FileConfiguration

/**
 * Configuration for the plugin.
 */
object Config {
    /**
     * Plugin file configuration, should be possible to reassign. We want /reload to be smooth as possible.
     */
    private lateinit var config: FileConfiguration

    object Discord {
        var Disabled
            get() = config.getBoolean("discord.disabled", true)
            set(value) = config.set("discord.disabled", value)

        val Token get() = config.getString("discord.token")!!
        val ServerAvatarUrl get() = config.getString("discord.server-avatar-url")!!
        val AvatarUrl get() = config.getString("discord.avatar-url")!!
        val GuildId get() = config.getLong("discord.guild.id")
        val GuildChannel get() = config.getLong("discord.guild.channel")
        val GuildWebhook get() = config.getString("discord.guild.webhook")!!
        val MessageFormat get() = config.getString("discord.format.message")!!
    }

    /**
     * Database configuration.
     */
    object Database {
        val JdbcUrl get() = config.getString("db.jdbc-url", "jdbc:sqlite:plugins/Yawa/yawa.db")!!
        val Username get() = config.getString("db.username", "root")!!
        val Password get() = config.getString("db.password", "")!!
    }

    /**
     * Keep alive feature configuration.
     */
    object KeepAlive {
        var Disabled
            get() = config.getBoolean("keep-alive.disabled", false)
            set(value) = config.set("keep-alive.disabled", value)
        val Timeout get() = config.getLong("keep-alive.timeout", 180)
    }

    /**
     * Permission feature configuration.
     */
    object Permission {
        var Disabled
            get() = config.getBoolean("permission.disabled", false)
            set(value) = config.set("permission.disabled", value)
    }

    /**
     * Tab list feature configuration.
     */
    object TabList {
        var Disabled
            get() = config.getBoolean("tab-list.disabled", false)
            set(value) = config.set("tab-list.disabled", value)
        val PlayerListName
            get() = config.getString(
                "tab-list.player-list-name",
                "<white>[</white><green><player-level></green><white>]</white> <player-name>"
            )!!
        val Header get() = config.getString("tab-list.header", "<gold>Yawa</gold>")!!
        val Footer get() = config.getString("tab-list.footer", "<server-player-count> / <server-player-max>")!!
    }

    /**
     * Ender dragon battle configuration.
     */
    object Ender {
        var Disabled
            get() = config.getBoolean("ender.disabled", false)
            set(value) = config.set("ender.disabled", value)
    }

    /**
     * Player head configuration.
     */
    object PlayerHead {
        var Disabled
            get() = config.getBoolean("player-head.disabled", false)
            set(value) = config.set("player-head.disabled", value)
    }

    /**
     * Essentials feature configuration.
     */
    object Essentials {
        var Disabled
            get() = config.getBoolean("essentials.disabled", false)
            set(value) = config.set("essentials.disabled", value)
    }

    /**
     * Ping feature configuration.
     */
    object Ping {
        var Disabled
            get() = config.getBoolean("ping.disabled", false)
            set(value) = config.set("ping.disabled", value)
    }

    /**
     * Sleep feature configuration.
     */
    object Sleep {
        var Disabled
            get() = config.getBoolean("sleep.disabled", false)
            set(value) = config.set("sleep.disabled", value)

        val TimeRate = config.getInt("sleep.time-rate", 85)
        val KickSeconds = config.getInt("sleep.kick-seconds", 15)

        object Chat {
            val Enabled get() = config.getBoolean("sleep.messages.chat.enabled", true)
        }

        object ActionBar {
            val Enabled get() = config.getBoolean("sleep.messages.action-bar.enabled", true)
        }
    }

    /**
     * Premium login feature configuration.
     */
    object Login {
        var Disabled
            get() = config.getBoolean("login.disabled", false)
            set(value) = config.set("login.disabled", value)
        val UsePremiumUuid get() = config.getBoolean("login.use-premium-uuid", true)
        val StrictNames get() = config.getBoolean("login.strict-names", false)
    }

    /**
     * Sit feature configuration.
     */
    object Sit {
        var Disabled
            get() = config.getBoolean("sit.disabled", false)
            set(value) = config.set("sit.disabled", value)
    }

    /**
     * Skin feature configuration.
     */
    object Skin {
        var Disabled
            get() = config.getBoolean("skin.disabled", false)
            set(value) = config.set("skin.disabled", value)

        object DefaultTexture {
            val DEFAULT_SKIN =
                "eyJ0aW1lc3RhbXAiOjE1ODc5NTIzNzQ4MDcsInByb2ZpbGVJZCI6IjBmNjQ3NDFlNDAzMjQ2ZGY5NDdmZmFiYTg5Yzc5ZGQwIiwicHJvZmlsZU5hbWUiOiJOb29vb29vb29vb29vbzAwIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80MzkzNDE4MzQ4NTY2NzMyMWM5YWQ1OGM0YWQwMzM3N2IwMjBhOGI3ODEwYzgyZWEzMDQ4YmVlOGYwYTI1OWM0In19fQ=="
            val DEFAULT_SKIN_SIGNATURE =
                "JPEl2xSJglzhykvvP/CRh5idRx0uO9qlJALDeQB7c0vds9NjzB8TXZhOdeDI8gxwCqKps6MSW1oh23yx9WKXbxt2HsmXfBmQYSV6HkJ+qWa//D8/Gi8osGa3MMi90CWRlalmZTGFYaF9ALimrLtBDBMKNnrV6MPdTaQbgRdIwUjRz6q/ST+qwR/HidrRowV23P/UNJd+/zl/mn2b4q+zpNpSsRqqlavHXrYI8aM7NtiNe6cJAafE8h6ibShCGFiaXDNpLYCzRcOWQ41HyTLFkhbeWauPw+aLCBvx49yCnhxtIlpXNnA4scmNTEruKLaleTYuj4PKqwqr6l/25NZgV08lcgfM3GpXgK4JQD/o3VYYAZvFjTBgrM0VBbKQCn+akLECi9bozRFWM9JPF6xrvYKLK0IlCX3bYCtdUtzHf2wfvVEoIp1XGdzYMPZUvRLUT22TO76bFyXPEOplNEEIhuQa1OpvXukLNmiibqLNsBD9fIdaMPRLw/A4SSC+yrU8LgGYCN4dco01vpapRBmqcjeVfP1YPaA+zUMscBHLgFlwCWSYsNMmwP89nAJOyM+UXFYNsZ5y7bEMwXimneFhEPxDqoIeHwtrbS3Gkiw40NdPDcQhTXMkvdlCKsoC7IbHsj2vTsZxTLKH5os29NjBeUS4/2DqpyfCCgfImXFhrEo="

            val Value get() = config.getString("skin.default-texture.value", DEFAULT_SKIN)!!
            val Signature get() = config.getString("skin.default-texture.signature", DEFAULT_SKIN_SIGNATURE)!!
        }
    }

    /**
     * Afk feature configuration.
     */
    object Afk {
        const val DEFAULT_NON_AFK = "<player-name>"
        const val DEFAULT_AFK = "<player-name> <yellow>AFK</yellow>"

        var Disabled
            get() = config.getBoolean("afk.disabled", false)
            set(value) = config.set("afk.disabled", value)
        val Seconds get() = config.getInt("afk.seconds", 120)
        val PlayerListName get() = config.getString("afk.player-list-name", DEFAULT_NON_AFK)!!
        val PlayerListNameAfk get() = config.getString("afk.player-list-name-afk", DEFAULT_AFK)!!
        val MessageEnabled get() = config.getBoolean("afk.messages.enabled", true)
    }

    /**
     * Chat feature configuration.
     */
    object Chat {
        const val DEFAULT_FORMAT_MESSAGE = "[<player-name>] <gold>»</gold> "
        const val DEFAULT_FORMAT_MESSAGE_TO =
            "[<light_purple><player-name></light_purple>] <light_purple>«</light_purple> "
        const val DEFAULT_FORMAT_MESSAGE_FROM =
            "[<light_purple><player-name></light_purple>] <light_purple>»</light_purple> "
        const val DEFAULT_FORMAT_MESSAGE_DISCORD = "[<aqua><player-name></aqua>] <aqua>»</aqua> "

        var Disabled
            get() = config.getBoolean("chat.disabled", false)
            set(value) = config.set("chat.disabled", value)

        val FormatMessage get() = config.getString("chat.format.message", DEFAULT_FORMAT_MESSAGE)!!
        val FormatMessageTo get() = config.getString("chat.format.message-to", DEFAULT_FORMAT_MESSAGE_TO)!!
        val FormatMessageFrom get() = config.getString("chat.format.message-from", DEFAULT_FORMAT_MESSAGE_FROM)!!
        val FormatMessageDiscord
            get() = config.getString(
                "chat.format.message-discord",
                DEFAULT_FORMAT_MESSAGE_DISCORD
            )!!
    }

    /**
     * Image feature configuration.
     */
    object Image {
        var Disabled
            get() = config.getBoolean("image.disabled", false)
            set(value) = config.set("image.disabled", value)
    }

    object Notify {
        var Disabled
            get() = config.getBoolean("notify.disabled", false)
            set(value) = config.set("notify.disabled", value)
        val Item: List<String>
            get() = config.getStringList("notify.item")
        val Debounce
            get() = config.getInt("notify.debounce", 10)
    }

    object HiddenArmor {
        var Disabled
            get() = config.getBoolean("hidden-armor.disabled", false)
            set(value) = config.set("hidden-armor.disabled", value)
    }

    object Chunk {
        var Disabled
            get() = config.getBoolean("chunk.disabled", false)
            set(value) = config.set("chunk.disabled", value)
    }

    /**
     * Load plugin configuration.
     */
    fun load(configuration: FileConfiguration) {
        config = configuration
    }

    /**
     * Set a config value.
     * @param path config path
     * @param value new value
     */
    fun set(path: String, value: Any) {
        config.set(path, value)
        Yawa.Instance.saveConfig()
    }

}