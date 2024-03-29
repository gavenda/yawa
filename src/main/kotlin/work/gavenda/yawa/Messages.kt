/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (c) 2022 Gavenda <gavenda@disroot.org>
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
 */

package work.gavenda.yawa

import com.comphenix.protocol.injector.temporary.TemporaryPlayer
import org.bukkit.entity.Player
import java.util.*

/**
 * Message key constants.
 */
object Message {
    const val AfkEntryMessage = "afk-entry"
    const val AfkLeaveMessage = "afk-leave"
    const val ActionBarSleeping = "action-bar-sleeping"
    const val ActionBarSleepingDone = "action-bar-sleeping-done"
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
    const val EnderBattleAlert = "ender-battle-alert"
    const val LoginInvalidSession = "login-invalid-session"
    const val LoginInvalidSessionRetry = "login-invalid-session-retry"
    const val LoginInvalidRequest = "login-invalid-request"
    const val LoginInvalidToken = "login-invalid-token"
    const val LoginInvalidSignature = "login-invalid-signature"
    const val LoginInvalidPublicKey = "login-invalid-public-key"
    const val LoginNameIllegal = "login-name-illegal"
    const val LoginNameShort = "login-name-short"
    const val LoginNameLong = "login-name-long"
    const val LoginError = "login-error"
    const val SleepKickMessage = "sleep-kick"
    const val SleepKickMessageBroadcast = "sleep-kick-broadcast"
    const val SleepKickRemainingBroadcast = "sleep-kick-remaining-broadcast"
    const val SleepKickAlert = "sleep-kick-alert"
    const val ChunkMarked = "chunk-marked"
    const val ChunkUnmarked = "chunk-unmarked"
    const val NotifyItemPickup = "notify-item-pickup"
    const val NotifyItemPickupRecent = "notify-item-pickup-recent"
    const val HiddenArmorVisible = "hidden-armor-visible"
    const val HiddenArmorInvisible = "hidden-armor-invisible"
    const val EssentialsWarpSet = "essentials-warp-set"
    const val EssentialsWarpTeleport = "essentials-warp-teleport"
    const val EssentialsWarpDelete = "essentials-warp-delete"
    const val EssentialsTeleportSpawn = "essentials-teleport-spawn"
    const val EssentialsHomeTeleport = "essentials-home-teleport"
    const val EssentialsHomeSet = "essentials-home-set"
    const val EssentialsTeleportDeath = "essentials-teleport-death"
    const val EssentialsTeleportErrorNoOverworld = "essentials-teleport-error-no-overworld"
    const val EssentialsTeleportErrorNoHome = "essentials-teleport-error-no-home"
    const val EssentialsTeleportErrorNoHomeWorld = "essentials-teleport-error-no-home-world"
    const val EssentialsTeleportErrorNoDeath = "essentials-teleport-error-no-death"
    const val EssentialsTeleportErrorNoLocation = "essentials-teleport-error-no-location"
    const val EssentialsGiveLevel = "essentials-give-level"
    const val EssentialsGiveLevelErrorNotEnoughLevel = "essentials-give-level-error-not-enough-level"
    const val EssentialsGiveLevelErrorInvalidLevel = "essentials-give-level-error-invalid-level"
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
    } else player.locale()
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