package work.gavenda.yawa.essentials

import work.gavenda.yawa.Config
import work.gavenda.yawa.Plugin

/**
 * Enable essentials feature.
 */
fun Plugin.enableEssentials() {
    if (Config.Essentials.Disabled) return
}

/**
 * Disable essentials feature.
 */
fun Plugin.disableEssentials() {
    if (Config.Essentials.Disabled) return
}