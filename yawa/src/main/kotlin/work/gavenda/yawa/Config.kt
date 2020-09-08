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

    object TabList {
        val Disabled get() = config.getBoolean("tab-list.disabled")
        val Header get() = config.getString("tab-list.header")!!
        val Footer get() = config.getString("tab-list.footer")!!
    }

    /**
     * Essentials feature configuration.
     */
    object Essentials {
        val Disabled get() = config.getBoolean("essentials.disabled")
    }

    /**
     * Ping feature configuration.
     */
    object Ping {
        val Disabled get() = config.getBoolean("ping.disabled")
    }

    /**
     * Sleep feature configuration.
     */
    object Sleep {
        val Disabled get() = config.getBoolean("sleep.disabled")

        object ActionBar {
            val Enabled get() = config.getBoolean("sleep.messages.action-bar.enabled")
        }

        object Chat {
            val Enabled get() = config.getBoolean("sleep.messages.chat.enabled")
        }
    }

    object Skin {
        val Disabled get() = config.getBoolean("skin.disabled")
    }

    object Afk {
        val Disabled get() = config.getBoolean("afk.disabled")
        val PlayerListName get() = config.getString("afk.player-list-name")!!
        val MessageEnabled get() = config.getBoolean("afk.messages.enabled")
    }

    object Chat {
        val Disabled get() = config.getBoolean("chat.disabled")
    }

    object Messages {
        val AfkEntryMessage get() = config.getString("messages.afk-entry")!!
        val AfkLeaveMessage get() = config.getString("messages.afk-leave")!!
        val ActionBarSleeping get() = config.getString("messages.action-bar-sleeping")!!
        val ActionBarSleepingDone get() = config.getString("messages.action-bar-sleeping-done")!!
        val PlayerEnterBed get() = config.getString("messages.player-enter-bed")!!
        val PlayerLeftBed get() = config.getString("messages.player-left-bed")!!
        val Sleeping: List<String> get() = config.getStringList("messages.chat-sleeping")
        val SleepingDone: List<String> get() = config.getStringList("messages.chat-sleeping-done")
        val PluginReload get() = config.getString("messages.plugin-reload")!!
        val PluginReloadConfig get() = config.getString("messages.plugin-reload-config")!!
        val FeatureDisabled get() = config.getString("messages.feature-disabled")!!
        val SkinApplied get() = config.getString("messages.skin-applied")!!
        val SkinGenerate get() = config.getString("messages.skin-generate")!!
        val SkinReject get() = config.getString("messages.skin-reject")!!
        val SkinRetrieve get() = config.getString("messages.skin-retrieve")!!
        val SkinNotFound get() = config.getString("messages.skin-not-found")!!
        val SkinRateLimit get() = config.getString("messages.skin-rate-limit")!!
        val SkinReset get() = config.getString("messages.skin-reset")!!
    }

    /**
     * Load plugin configuration.
     */
    fun load(configuration: FileConfiguration) {
        config = configuration
    }

}