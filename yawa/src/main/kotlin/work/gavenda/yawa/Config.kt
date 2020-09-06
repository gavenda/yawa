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
            val Sleeping: List<String> get() = config.getStringList("sleep.messages.chat.sleeping")
            val SleepingDone: List<String> get() = config.getStringList("sleep.messages.chat.sleeping-done")
        }
    }

    object Skin {
        val Disabled = config.getBoolean("skin.disabled", true)
    }

    object Afk {
        val Disabled = config.getBoolean("afk.disabled", false)
        val MessageEnabled get() = config.getBoolean("afk.messages.enabled")
        val EntryMessage get() = config.getString("afk.messages.entry")!!
        val LeaveMessage get() = config.getString("afk.messages.leave")!!
    }

    /**
     * Load plugin configuration.
     */
    fun load(configuration: FileConfiguration) {
        config = configuration
    }

}