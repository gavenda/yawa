/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (C) 2020 Gavenda <gavenda@disroot.org>
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

import com.comphenix.protocol.async.AsyncListenerHandler
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.*
import java.security.KeyPair

/**
 * Represents the login feature.
 */
object LoginFeature : PluginFeature {
    override val isDisabled get() = Config.Login.Disabled

    private val keyPair: KeyPair = MinecraftEncryption.generateKeyPair()
    private lateinit var loginHandler: AsyncListenerHandler
    private lateinit var loginEncryptionHandler: AsyncListenerHandler

    override fun enable() {
        super.enable()
        if (isDisabled) return
        if (server.onlineMode) {
            yawaLogger.warn("Server is in online mode, rendering this feature useless")
            return
        }
    }

    override fun disable() {
        if (isDisabled) return
        if (server.onlineMode) return
        super.disable()
    }

    override fun createTables() {
        transaction {
            SchemaUtils.create(PlayerLoginSchema)
        }
    }

    override fun registerEventListeners() {
        loginHandler = protocolManager
            .asynchronousManager
            .registerAsyncHandler(LoginListener(plugin, keyPair))
            .apply { start() }
        loginEncryptionHandler = protocolManager
            .asynchronousManager
            .registerAsyncHandler(LoginEncryptionListener(plugin, keyPair))
            .apply { start() }

    }

    override fun unregisterEventListeners() {
        protocolManager
            .asynchronousManager
            .unregisterAsyncHandler(loginHandler)

        protocolManager
            .asynchronousManager
            .unregisterAsyncHandler(loginEncryptionHandler)

        // Invalidate session cache
        Session.invalidateAll()
    }

}