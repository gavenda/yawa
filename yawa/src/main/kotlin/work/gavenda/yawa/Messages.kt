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

import com.comphenix.protocol.injector.server.TemporaryPlayer
import org.bukkit.entity.Player
import work.gavenda.yawa.api.localeCompat
import java.util.*

/**
 * Message key constants.
 */
object Message {
    const val AfkEntryMessage = "afk-entry"
    const val AfkLeaveMessage = "afk-leave"
    const val PlayerAfkStart = "player-afk-start"
    const val PlayerAfkEnd = "player-afk-end"
    const val PlayerEnterBed = "player-enter-bed"
    const val PlayerLeftBed = "player-left-bed"
    const val PlayerSitStart = "player-sit-start"
    const val PlayerSitEnd = "player-sit-end"
    const val Sleeping = "chat-sleeping"
    const val SleepingDone = "chat-sleeping-done"
    const val PluginReload = "plugin-reload"
    const val PluginReloadConfig = "plugin-reload-config"
    const val PermissionPlayerNotFound = "permission-player-not-found"
    const val PermissionPlayerNotLoggedIn = "permission-player-not-logged-in"
    const val PermissionGroupNotFound = "permission-group-not-found"
    const val PermissionApplied = "permission-applied"
    const val PlayerPingResponse = "player-ping-response"
    const val FeatureDisabled = "feature-disabled"
    const val FeatureSetDisabled = "feature-set-disabled"
    const val FeatureSetEnabled = "feature-set-enabled"
    const val FeatureValueInvalid = "feature-value-invalid"
    const val WhisperPlayerNotFound = "whisper-player-not-found"
    const val SkinApplied = "skin-applied"
    const val SkinGenerate = "skin-generate"
    const val SkinReject = "skin-reject"
    const val SkinRetrieve = "skin-retrieve"
    const val SkinNotFound = "skin-not-found"
    const val SkinRateLimit = "skin-rate-limit"
    const val SkinReset = "skin-reset"
    const val EnderBattleStart = "ender-battle-start"
    const val EnderBattleTeleport = "ender-battle-teleport"
    const val LoginInvalidSession = "login-invalid-session"
    const val LoginInvalidSessionRetry = "login-invalid-session-retry"
    const val LoginInvalidRequest = "login-invalid-request"
    const val LoginInvalidToken = "login-invalid-token"
    const val LoginNameIllegal = "login-name-illegal"
    const val LoginNameShort = "login-name-short"
    const val LoginNameLong = "login-name-long"
    const val LoginError = "login-error"
    const val SleepKickMessage = "sleep-kick"
    const val SleepKickMessageBroadcast = "sleep-kick-broadcast"
    const val ImageUploadInvalid = "image-upload-invalid"
    const val ImageUploadError = "image-upload-error"
    const val ImageUploadSuccess = "image-upload-success"
    const val ImageUploadBegin = "image-upload-begin"
    const val ChunkMarked = "chunk-marked"
    const val ChunkUnmarked = "chunk-unmarked"
}

/**
 * Simple messages helper for players with different locales.
 */
object Messages {

    /**
     * Use server system locale.
     */
    fun useDefault(): MessagesContext = MessagesDefaultContext()

    /**
     * Use player locale.
     */
    fun forPlayer(player: Player): MessagesContext = MessagesPlayerContext(player)

}

interface MessagesContext {
    /**
     * Get the message given the key.
     * @param key the key
     */
    fun get(key: String): String

    /**
     * Get a list of messages given the key.
     * @param key the key
     */
    fun getList(key: String): List<String>
}

class MessagesDefaultContext : MessagesContext {
    private val messages = ResourceBundle.getBundle("i18n.messages", Locale.getDefault())

    /**
     * Get the message given the key.
     * @param key the key
     */
    override fun get(key: String): String = messages.getString(key)

    /**
     * Get a list of messages given the key.
     * @param key the key
     */
    override fun getList(key: String) = messages.getStringArray(key).toList()
}

/**
 * Messages in a player context.
 * @param player the player to get messages from
 */
class MessagesPlayerContext(player: Player) : MessagesContext {
    private val locale = if (player is TemporaryPlayer) {
        Locale.getDefault()
    } else player.localeCompat()
    private val messages = ResourceBundle.getBundle("i18n.messages", locale)

    /**
     * Get the message given the key.
     * @param key the key
     */
    override fun get(key: String): String = messages.getString(key)

    /**
     * Get a list of messages given the key.
     * @param key the key
     */
    override fun getList(key: String) = messages.getStringArray(key).toList()
}