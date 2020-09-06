package work.gavenda.yawa.afk

import org.bukkit.event.HandlerList
import work.gavenda.yawa.Config
import work.gavenda.yawa.DisabledCommand
import work.gavenda.yawa.Plugin
import work.gavenda.yawa.api.*
import java.util.concurrent.TimeUnit

private var afkTaskId = -1
private val afkListener = AfkListener()

/**
 * Enable afk feature.
 */
fun Plugin.enableAfk() {
    if (Config.Afk.Disabled) {
        getCommand("afk")?.setExecutor(DisabledCommand())
        return
    }

    // Command handler
    getCommand("afk")?.setExecutor(AfkCommand())

    // Tasks
    afkTaskId = bukkitTimerTask(this, 0, 20) {
        server.onlinePlayers.forEach { player ->
            val afkDelta = System.currentTimeMillis() - player.lastInteractionMillis
            val afkSeconds = TimeUnit.MILLISECONDS.toSeconds(afkDelta)
            val isNotAfk = !player.isAfk

            if (isNotAfk && afkSeconds > 30) {
                player.isAfk = true

                val message = Placeholder
                    .withContext(player)
                    .parse(Config.Afk.EntryMessage)

                player.world.broadcastMessageIf(message) {
                    Config.Afk.MessageEnabled
                }
            }
        }
    }

    // Register events
    server.pluginManager.registerEvents(afkListener, this)
}

/**
 * Disable afk feature.
 */
fun Plugin.disableAfk() {
    if (Config.Afk.Disabled) return

    // Events
    HandlerList.unregisterAll(afkListener)
    // Tasks
    server.scheduler.cancelTask(afkTaskId)
    // Command handlers
    getCommand("afk")?.setExecutor(null)
}