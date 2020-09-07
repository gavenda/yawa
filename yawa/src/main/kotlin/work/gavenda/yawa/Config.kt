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
        val JdbcUrl get() = config.getString("db.jdbc-url")
        val Username get() = config.getString("db.username")
        val Password get() = config.getString("db.password")
    }

    object Essentials {
        val Disabled get() = config.getBoolean("essentials.disabled")
    }

    /**
     * Ping feature configuration.
     */
    object Ping {
        val Disabled get() = config.getBoolean("ping.disabled")
        val ServerName get() = config.getString("ping.server-name")
    }

    /**
     * Sleep feature configuration.
     */
    object Sleep {
        val Disabled get() = config.getBoolean("sleep.disabled")

        object ActionBar {
            val Enabled get() = config.getBoolean("sleep.messages.action-bar.enabled")
            val Sleeping get() = config.getString("sleep.messages.action-bar.sleeping")!!
            val SleepingDone get() = config.getString("sleep.messages.action-bar.sleeping-done")!!
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
        val Disabled get() = config.getBoolean("skin.disabled")
    }

    object Afk {
        val Disabled get() = config.getBoolean("afk.disabled")
        val MessageEnabled get() = config.getBoolean("afk.messages.enabled")
        val EntryMessage get() = config.getString("afk.messages.entry")!!
        val LeaveMessage get() = config.getString("afk.messages.leave")!!
    }

    object Chat {
        val Disabled get() = config.getBoolean("chat.disabled")
    }

    /**
     * Load plugin configuration.
     */
    fun load(configuration: FileConfiguration) {
        config = configuration
    }

}