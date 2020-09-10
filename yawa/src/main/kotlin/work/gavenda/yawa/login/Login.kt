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
import work.gavenda.yawa.Config
import work.gavenda.yawa.Plugin

private lateinit var loginListener: LoginListener

fun Plugin.enableLogin() {
    if (Config.Login.Disabled) return
    if (server.onlineMode) {
        slF4JLogger.warn("Server is in online mode, rendering this feature useless")
        return
    }

    loginListener = LoginListener(this)

    ProtocolLibrary.getProtocolManager()
        .asynchronousManager
        .registerAsyncHandler(loginListener)
        .start()
}

fun Plugin.disableLogin() {
    if (Config.Login.Disabled) return
    if (server.onlineMode) return

    ProtocolLibrary.getProtocolManager()
        .asynchronousManager
        .unregisterAsyncHandler(loginListener)

    Session.invalidateAll()
}