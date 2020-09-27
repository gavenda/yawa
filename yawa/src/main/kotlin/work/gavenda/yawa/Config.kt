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
     * Plugin file configuration, should be possible to reassign. We want /reload to be smooth as possible.
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
     * Keep alive feature configuration.
     */
    object KeepAlive {
        var Disabled
            get() = config.getBoolean("keep-alive.disabled")
            set(value) = config.set("keep-alive.disabled", value)
        val Timeout get() = config.getLong("keep-alive.timeout")
    }

    /**
     * Permission feature configuration.
     */
    object Permission {
        var Disabled
            get() = config.getBoolean("permission.disabled")
            set(value) = config.set("permission.disabled", value)
    }

    /**
     * Tab list feature configuration.
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
     * Player head configuration.
     */
    object PlayerHead {
        var Disabled
            get() = config.getBoolean("player-head.disabled")
            set(value) = config.set("player-head.disabled", value)
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

    /**
     * Premium login feature configuration.
     */
    object Login {
        var Disabled
            get() = config.getBoolean("login.disabled")
            set(value) = config.set("login.disabled", value)
        val UsePremiumUuid get() = config.getBoolean("login.use-premium-uuid")
    }

    /**
     * Sit feature configuration.
     */
    object Sit {
        var Disabled
            get() = config.getBoolean("sit.disabled")
            set(value) = config.set("sit.disabled", value)
    }

    /**
     * Skin feature configuration.
     */
    object Skin {
        var Disabled
            get() = config.getBoolean("skin.disabled")
            set(value) = config.set("skin.disabled", value)

        object DefaultTexture {
            val Value get() = config.getString("skin.default-texture.value")!!
            val Signature get() = config.getString("skin.default-texture.signature")!!
        }
    }

    /**
     * Afk feature configuration.
     */
    object Afk {
        var Disabled
            get() = config.getBoolean("afk.disabled")
            set(value) = config.set("afk.disabled", value)
        val Seconds get() = config.getInt("afk.seconds")
        val PlayerListName get() = config.getString("afk.player-list-name")!!
        val MessageEnabled get() = config.getBoolean("afk.messages.enabled")
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