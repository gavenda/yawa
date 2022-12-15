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

package work.gavenda.yawa.api.mojang

import com.google.common.cache.CacheBuilder
import com.google.common.util.concurrent.RateLimiter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import work.gavenda.yawa.api.apiLogger
import work.gavenda.yawa.api.asHttpConnection
import work.gavenda.yawa.api.asText
import java.net.InetAddress
import java.net.URL
import java.net.URLEncoder
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Provides a simplistic way to access the Mojang API.
 */
@Suppress("UnstableApiUsage")
object MojangApi {
    private const val HTTP_NO_CONTENT = 204
    private const val HTTP_TOO_MANY_REQUESTS = 429

    private const val URI_API_STATUS = "https://status.mojang.com/check"
    private const val URI_API_USERNAME_UUID = "https://api.mojang.com/users/profiles/minecraft"
    private const val URI_API_PROFILE = "https://sessionserver.mojang.com/session/minecraft/profile"
    private const val URI_API_HAS_JOIN = "https://sessionserver.mojang.com/session/minecraft/hasJoined"

    // Mojang API rate limiter, does not apply to profile since it can be as many as long as its unique
    private val rateLimiter = RateLimiter.create(600.0, 10, TimeUnit.MINUTES)
    private val json = Json {
        ignoreUnknownKeys = true
    }

    // We cache to avoid hitting the 1 minute rate limit per profile
    private val profileCache = CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .build<UUID, MojangProfile>()

    /**
     * Retrieves all Mojang services.
     * @return a list of [MojangService]
     */
    fun findServiceStatus(): List<MojangService> {
        if (!rateLimiter.tryAcquire()) {
            throw RateLimitException()
        }

        val response = URL(URI_API_STATUS).asText()
        return json.decodeFromString(response)
    }

    /**
     * Retrieves Minecraft UUID given the username.
     * @param username minecraft username
     * @return an instance of [UUID], will return null when not found
     */
    fun findUuidByName(username: String): UUID? {
        if (!rateLimiter.tryAcquire()) {
            throw RateLimitException()
        }

        val httpConnection = URL("$URI_API_USERNAME_UUID/$username").asHttpConnection()

        if (httpConnection.responseCode == HTTP_NO_CONTENT) {
            return null
        }
        if (httpConnection.responseCode == HTTP_TOO_MANY_REQUESTS) {
            throw RateLimitException()
        }

        return json.decodeFromString<MojangProfile>(httpConnection.asText()).id
    }

    /**
     * Retrieves the minecraft profile by confirming the username, server hash, and host ip.
     */
    fun hasJoined(username: String, serverHash: String, hostIp: InetAddress): MojangProfile? {
        // apiLogger.info("hasJoined, username: $username, serverHash: $serverHash, hostIp: ${hostIp.hostAddress}")
        // val encodedIP = URLEncoder.encode(hostIp.hostAddress, "UTF-8")
        val httpConnection = URL("$URI_API_HAS_JOIN?username=$username&serverId=$serverHash")
            .asHttpConnection()
        val responseCode = httpConnection.responseCode

        if (responseCode == HTTP_NO_CONTENT) {
            return null
        }

        return json.decodeFromString<MojangProfile>(httpConnection.asText())
    }

    /**
     * Retrieves the Mojang profile of the given user uuid.
     * @param uuid minecraft user uuid
     * @return an instance of [MojangProfile], will return null when not found
     */
    fun findProfile(uuid: UUID): MojangProfile? {
        val cachedProfile = profileCache.getIfPresent(uuid)

        // Return cache immediately if available
        if (cachedProfile != null) {
            return cachedProfile
        }
        // Or we continue our business

        val uuidNoDash = uuid.toString().replace("-", "")
        val httpConnection = URL("$URI_API_PROFILE/$uuidNoDash?unsigned=false").asHttpConnection()

        // If we actually hit this, we have a bad cache implementation
        if (httpConnection.responseCode == HTTP_TOO_MANY_REQUESTS) {
            apiLogger.warn("We have actually hit the HTTP 429 response, please report it to the developer")
            throw RateLimitException()
        }

        return json.decodeFromString<MojangProfile>(httpConnection.asText()).also {
            // We cache the result
            profileCache.put(uuid, it)
        }
    }
}