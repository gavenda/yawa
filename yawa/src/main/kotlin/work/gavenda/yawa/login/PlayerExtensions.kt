/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (C) 2021 Gavenda <gavenda@disroot.org>
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

import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Verified state.
 * @return true if verified player (legit minecraft)
 */
val Player.isVerified: Boolean
    get() = transaction {
        val uuid = name.minecraftOfflineUuid()
        val login = PlayerLogin.findById(uuid) ?: return@transaction false

        return@transaction login.premium
    }

