/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 *  Copyright (C) 2021 Gavenda <gavenda@disroot.org>
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
 *
 */

package work.gavenda.yawa

import work.gavenda.yawa.api.PluginEnvironment
import work.gavenda.yawa.api.pluginEnvironment

/**
 * Constants for features.
 */
object Feature {
    const val AFK = "afk"
    const val CHAT = "chat"
    const val ESSENTIALS = "essentials"
    const val ENDER = "ender"
    const val IMAGE_UPLOAD = "img-up"
    const val LOGIN = "login"
    const val PERMISSION = "permission"
    const val PING = "ping"
    const val PLAYER_HEAD = "player-head"
    const val SIT = "sit"
    const val SKIN = "skin"
    const val SLEEP = "sleep"
    const val TABLIST = "tab-list"
    const val KEEP_ALIVE = "keep-alive"
}

/**
 * Represents a feature in the plugin.
 */
interface PluginFeature {

    /**
     * Return true if feature is disabled, otherwise false.
     */
    val isDisabled: Boolean

    /**
     * Enable this feature.
     */
    fun enable() {
        if (isDisabled) {
            disableCommands()
            return
        }

        createTables()
        registerHooks()
        registerPlaceholders()
        registerEventListeners()

        if (pluginEnvironment == PluginEnvironment.PAPER) {
            registerPaperEventListeners()
        } else {
            registerBukkitEventListeners()
        }

        registerTasks()
        enableCommands()
        onEnable()
    }

    /**
     * Disable this feature.
     */
    fun disable() {
        if (isDisabled) return

        onDisable()
        disableCommands()
        unregisterTasks()
        unregisterEventListeners()

        if (pluginEnvironment == PluginEnvironment.PAPER) {
            unregisterPaperEventListeners()
        } else {
            unregisterBukkitEventListeners()
        }

        unregisterPlaceholders()
        unregisterHooks()
    }

    /**
     * Called when feature is enabled.
     */
    fun onEnable() {}

    /**
     * Called when feature is disabled.
     */
    fun onDisable() {}

    /**
     * Create feature tables.
     */
    fun createTables() {}

    /**
     * Enable feature commands.
     */
    fun enableCommands() {}

    /**
     * Register plugin hooks.
     */
    fun registerHooks() {}

    /**
     * Register feature placeholders.
     */
    fun registerPlaceholders() {}

    /**
     * Register cross-compatible feature event listeners.
     */
    fun registerEventListeners() {}

    /**
     * Register paper-only event listeners.
     */
    fun registerPaperEventListeners() {}

    /**
     * Register bukkit-only event listeners.
     */
    fun registerBukkitEventListeners() {}

    /**
     * Register feature tasks.
     */
    fun registerTasks() {}

    /**
     * Disable feature commands.
     */
    fun disableCommands() {}

    /**
     * Register plugin hooks.
     */
    fun unregisterHooks() {}

    /**
     * Unregister feature placeholders.
     */
    fun unregisterPlaceholders() {}

    /**
     * Unregister feature tasks.
     */
    fun unregisterTasks() {}

    /**
     * Unregister feature event listeners.
     */
    fun unregisterEventListeners() {}

    /**
     * Unregister paper-only event listeners.
     */
    fun unregisterPaperEventListeners() {}

    /**
     * Unregister bukkit-only event listeners.
     */
    fun unregisterBukkitEventListeners() {}
}