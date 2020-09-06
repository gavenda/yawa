package work.gavenda.yawa.skin

import org.bukkit.event.HandlerList
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.Config
import work.gavenda.yawa.DisabledCommand
import work.gavenda.yawa.Plugin
import work.gavenda.yawa.api.bukkitAsyncTask

private val skinListener = SkinListener()

/**
 * Enable skin feature.
 */
fun Plugin.enableSkin() {
    if (Config.Skin.Disabled) {
        getCommand("skin")?.setExecutor(DisabledCommand())
        return
    }

    // Init tables if not created
    bukkitAsyncTask(this) {
        transaction {
            SchemaUtils.create(PlayerTextureSchema)
        }
    }

    // Register event listeners
    server.pluginManager.registerEvents(skinListener, this)

    val skinCommand = SkinCommand().apply {
        sub(SkinPlayerCommand(), "player")
        sub(SkinResetCommand(), "reset")
        sub(SkinUrlCommand(), "url")
    }

    getCommand("skin")?.setExecutor(skinCommand)
}

/**
 * Disable skin feature.
 */
fun Plugin.disableSkin() {
    if (Config.Skin.Disabled) return

    getCommand("skin")?.setExecutor(null)

    // Unregister event listeners
    HandlerList.unregisterAll(skinListener)
}