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

import org.bukkit.configuration.file.FileConfiguration

/**
 * Configuration for the plugin.
 */
object Config {
    /**
     * Spigot plugin file configuration, should be possible to reassign. We want /reload to be smooth as possible.
     */
    private lateinit var config: FileConfiguration

    /**
     * Database configuration.
     */
    object Database {
        val JdbcUrl get() = config.getString("db.jdbc-url")
        val Username get() = config.getString("db.username")
        val Password get() = config.getString("db.password")
    }

    /**
     * Tab list configuration.
     */
    object TabList {
        var Disabled
            get() = config.getBoolean("tab-list.disabled")
            set(value) = config.set("tab-list.disabled", value)
        val Header get() = config.getString("tab-list.header")!!
        val Footer get() = config.getString("tab-list.footer")!!
    }

    /**
     * Ender dragon battle configuration.
     */
    object Ender {
        var Disabled
            get() = config.getBoolean("ender.disabled")
            set(value) = config.set("ender.disabled", value)
    }

    /**
     * Essentials feature configuration.
     */
    object Essentials {
        var Disabled
            get() = config.getBoolean("essentials.disabled")
            set(value) = config.set("essentials.disabled", value)
    }

    /**
     * Ping feature configuration.
     */
    object Ping {
        var Disabled
            get() = config.getBoolean("ping.disabled")
            set(value) = config.set("ping.disabled", value)
    }

    /**
     * Sleep feature configuration.
     */
    object Sleep {
        var Disabled
            get() = config.getBoolean("sleep.disabled")
            set(value) = config.set("sleep.disabled", value)

        val TimeRate = config.getInt("sleep.time-rate")

        object ActionBar {
            val Enabled get() = config.getBoolean("sleep.messages.action-bar.enabled")
        }

        object Chat {
            val Enabled get() = config.getBoolean("sleep.messages.chat.enabled")
        }
    }

    object Login {
        var Disabled
            get() = config.getBoolean("login.disabled")
            set(value) = config.set("login.disabled", value)
        val UsePremiumUuid get() = config.getBoolean("login.use-premium-uuid")
    }

    object Sit {
        var Disabled
            get() = config.getBoolean("sit.disabled")
            set(value) = config.set("sit.disabled", value)
    }

    object Skin {
        var Disabled
            get() = config.getBoolean("skin.disabled")
            set(value) = config.set("skin.disabled", value)

        object DefaultTexture {
            val Value get() = config.getString("skin.default-texture.value")!!
            val Signature get() = config.getString("skin.default-texture.signature")!!
        }
    }

    object Afk {
        var Disabled
            get() = config.getBoolean("afk.disabled")
            set(value) = config.set("afk.disabled", value)
        val Seconds get() = config.getInt("afk.seconds")
        val PlayerListName get() = config.getString("afk.player-list-name")!!
        val MessageEnabled get() = config.getBoolean("afk.messages.enabled")
    }

    object Messages {
        val AfkEntryMessage get() = config.getString("messages.afk-entry")!!
        val AfkLeaveMessage get() = config.getString("messages.afk-leave")!!
        val ActionBarSleeping get() = config.getString("messages.action-bar-sleeping")!!
        val ActionBarSleepingDone get() = config.getString("messages.action-bar-sleeping-done")!!
        val PlayerAfkStart get() = config.getString("messages.player-afk-start")!!
        val PlayerAfkEnd get() = config.getString("messages.player-afk-end")!!
        val PlayerEnterBed get() = config.getString("messages.player-enter-bed")!!
        val PlayerLeftBed get() = config.getString("messages.player-left-bed")!!
        val PlayerSitStart get() = config.getString("messages.player-sit-start")!!
        val PlayerSitEnd get() = config.getString("messages.player-sit-end")!!
        val Sleeping: List<String> get() = config.getStringList("messages.chat-sleeping")
        val SleepingDone: List<String> get() = config.getStringList("messages.chat-sleeping-done")
        val PluginReload get() = config.getString("messages.plugin-reload")!!
        val PluginReloadConfig get() = config.getString("messages.plugin-reload-config")!!
        val FeatureDisabled get() = config.getString("messages.feature-disabled")!!
        val FeatureSetDisabled get() = config.getString("messages.feature-set-disabled")!!
        val FeatureSetEnabled get() = config.getString("messages.feature-set-enabled")!!
        val SkinApplied get() = config.getString("messages.skin-applied")!!
        val SkinGenerate get() = config.getString("messages.skin-generate")!!
        val SkinReject get() = config.getString("messages.skin-reject")!!
        val SkinRetrieve get() = config.getString("messages.skin-retrieve")!!
        val SkinNotFound get() = config.getString("messages.skin-not-found")!!
        val SkinRateLimit get() = config.getString("messages.skin-rate-limit")!!
        val SkinReset get() = config.getString("messages.skin-reset")!!
        val EnderBattleStart get() = config.getString("messages.ender-battle-start")!!
        val EnderBattleTeleport get() = config.getString("messages.ender-battle-teleport")!!
        val LoginInvalidSession get() = config.getString("messages.login-invalid-session")!!
        val LoginInvalidSessionRetry get() = config.getString("messages.login-invalid-session-retry")!!
        val LoginInvalidRequest get() = config.getString("messages.login-invalid-request")!!
        val LoginInvalidToken get() = config.getString("messages.login-invalid-token")!!
        val LoginNameIllegal get() = config.getString("messages.login-name-illegal")!!
        val LoginNameShort get() = config.getString("messages.login-name-short")!!
        val LoginNameLong get() = config.getString("messages.login-name-long")!!
        val LoginError get() = config.getString("messages.login-error")!!
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
        Plugin.Instance.saveConfig()
    }

}