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
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import work.gavenda.yawa.Config
import work.gavenda.yawa.Plugin
import work.gavenda.yawa.api.bukkitAsyncTask
import work.gavenda.yawa.skin.PlayerTextureSchema
import work.gavenda.yawa.skin.enableSkin
import java.security.KeyPair

private val protocolManager = ProtocolLibrary.getProtocolManager()

private lateinit var loginListener: LoginListener
private lateinit var loginEncryptionListener: LoginEncryptionListener

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

    loginListener = LoginListener(this)
    loginEncryptionListener = LoginEncryptionListener(this)

    protocolManager
        .asynchronousManager
        .registerAsyncHandler(loginListener)
        .start()

    protocolManager
        .asynchronousManager
        .registerAsyncHandler(loginEncryptionListener)
        .start()
}

/**
 * Disable premium login feature.
 */
fun Plugin.disableLogin() {
    if (Config.Login.Disabled) return
    if (server.onlineMode) return

    try {
        protocolManager
            .asynchronousManager
            .unregisterAsyncHandler(loginEncryptionListener)

        protocolManager
            .asynchronousManager
            .unregisterAsyncHandler(loginListener)
    } catch (e: NullPointerException) {
        slF4JLogger.warn("Unable to unregister handlers, perhaps plugin was reloaded")
    }

    Session.invalidateAll()
}