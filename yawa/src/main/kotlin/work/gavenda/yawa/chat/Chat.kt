package work.gavenda.yawa.chat

import work.gavenda.yawa.Config
import work.gavenda.yawa.Plugin

/**
 * Enable chat feature.
 */
fun Plugin.enableChat() {
    if(Config.Chat.Disabled) return
}

/**
 * Disable chat feature.
 */
fun Plugin.disableChat() {
    if(Config.Chat.Disabled) return
}