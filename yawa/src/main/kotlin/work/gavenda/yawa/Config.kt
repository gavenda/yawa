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
     * Database configuration
     */
    object Database {
        val JdbcUrl = config.getString("db.jdbc-url")
        val Username = config.getString("db.username")
        val Password = config.getString("db.password")
    }

    /**
     * Ping feature configuration.
     */
    object Ping {
        val Disabled = config.getBoolean("ping.disabled", false)
        val ServerName = config.getString("ping.server-name", "")
    }

    /**
     * Sleep feature configuration.
     */
    object Sleep {
        val Disabled get() = config.getBoolean("sleep.disabled")

        object ActionBar {
            val Enabled get() = config.getBoolean("sleep.messages.action-bar.enabled")
            val PlayerSleeping get() = config.getString("sleep.messages.action-bar.player-sleeping")!!
            val NightSkipping get() = config.getString("sleep.messages.action-bar.night-skipping")!!
        }

        object Chat {
            val Enabled get() = config.getBoolean("sleep.messages.chat.enabled")
            val PlayerSleeping get() = config.getString("sleep.messages.chat.player-sleeping")!!
            val PlayerLeftBed get() = config.getString("sleep.messages.chat.player-left-bed")!!
            val NightSkipping: List<String> get() = config.getStringList("sleep.messages.chat.night-skipping")
            val NightSkipped: List<String> get() = config.getStringList("sleep.messages.chat.night-skipped")
        }
    }

    object Skin {
        val Disabled = config.getBoolean("skin.disabled", true)
    }

    object Afk {
        val Disabled = config.getBoolean("afk.disabled", false)
        val EntryMessage get() = config.getString("afk.entry-message")!!
        val LeaveMessage get() = config.getString("afk.leave-message")!!
    }

    /**
     * Load plugin configuration.
     */
    fun load(configuration: FileConfiguration) {
        config = configuration
    }

}