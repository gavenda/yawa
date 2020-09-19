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

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.async.AsyncListenerHandler
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.Config
import work.gavenda.yawa.Plugin
import java.security.KeyPair

private val protocolManager = ProtocolLibrary.getProtocolManager()

private lateinit var loginHandler: AsyncListenerHandler
private lateinit var loginEncryptionHandler: AsyncListenerHandler

val keyPair: KeyPair = MinecraftEncryption.generateKeyPair()

/**
 * Enable premium login feature.
 */
fun Plugin.enableLogin() {
    if (Config.Login.Disabled) return
    if (server.onlineMode) {
        slF4JLogger.warn("Server is in online mode, rendering this feature useless")
        return
    }

    // Init tables if not created
    transaction {
        SchemaUtils.create(UserLoginSchema)
    }

    loginHandler = protocolManager
        .asynchronousManager
        .registerAsyncHandler(LoginListener(this))
        .apply { start() }

    loginEncryptionHandler = protocolManager
        .asynchronousManager
        .registerAsyncHandler(LoginEncryptionListener(this))
        .apply { start() }
}

/**
 * Disable premium login feature.
 */
fun Plugin.disableLogin() {
    if (Config.Login.Disabled) return
    if (server.onlineMode) return

    protocolManager
        .asynchronousManager
        .unregisterAsyncHandler(loginHandler)

    protocolManager
        .asynchronousManager
        .unregisterAsyncHandler(loginEncryptionHandler)

    Session.invalidateAll()
}