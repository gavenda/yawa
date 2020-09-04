package work.gavenda.yawa.skin

import work.gavenda.yawa.Config
import work.gavenda.yawa.DisabledCommand
import work.gavenda.yawa.Plugin

/**
 * Enable skin feature.
 */
fun Plugin.enableSkin() {
    if (Config.Skin.Disabled) {
        getCommand("skin")?.setExecutor(DisabledCommand())
        return
    }

    val skinCommand = SkinCommand()
    skinCommand.sub(SkinPlayerCommand(), "player")

    getCommand("skin")?.setExecutor(skinCommand)
}

/**
 * Disable skin feature.
 */
fun Plugin.disableSkin() {
    if (Config.Skin.Disabled) return

    getCommand("skin")?.setExecutor(null)
}