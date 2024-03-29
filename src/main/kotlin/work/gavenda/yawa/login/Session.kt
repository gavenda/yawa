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

package work.gavenda.yawa.login

import com.google.common.cache.CacheBuilder
import work.gavenda.yawa.logger
import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

/**
 * A simple session store for temporarily storing a login session.
 */
object Session {

    private val sessionCache = CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .build<String, LoginSession>()

    private val pendingSession = CacheBuilder.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build<String, String>()

    /**
     * Find an existing session.
     * @param address socket address of session
     */
    fun find(address: InetSocketAddress) = sessionCache.getIfPresent(address.asSessionId())

    /**
     * Checks if logging in user has a pending session.
     */
    fun hasPendingSession(address: InetSocketAddress, name: String): Boolean {
        return pendingSession.getIfPresent(address.address.hostAddress) == name
    }

    /**
     * Mark address and logging in user as pending.
     */
    fun pending(address: InetSocketAddress, name: String) {
        logger.info("Pending session, address: $address, name: $name")
        pendingSession.put(address.address.hostAddress, name)
    }

    /**
     * Cache a session.
     * @param address socket address of session
     */
    fun cache(address: InetSocketAddress, session: LoginSession) {
        logger.info("Caching session, address: $address, name: $session")
        sessionCache.put(address.asSessionId(), session)
    }

    /**
     * Invalidate a session in the cache.
     */
    fun invalidate(address: InetSocketAddress) {
        sessionCache.invalidate(address.asSessionId())
    }

    /**
     * Invalidate all sessions.
     */
    fun invalidateAll() {
        sessionCache.invalidateAll()
    }

}

/**
 * Formats this socket address as a session identifier.
 */
fun InetSocketAddress.asSessionId(): String {
    return "${address.hostAddress}:$port"
}