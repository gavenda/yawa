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

package work.gavenda.yawa.api.mojang

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

class MojangApiTest {
    @Test
    @DisplayName("Test Mojang API")
    fun testMojangAPI() {
        val expectedUuidStr = "542189cf68fb415bbbe6c60da626e65a"
        val expectedUuid = UUID.fromString("542189cf-68fb-415b-bbe6-c60da626e65a")
        val playerName = "Red"

        val playerUuid = MojangApi.findUuidByName(playerName)

        assertEquals(expectedUuid, playerUuid)

        val playerProfile = MojangApi.findProfile(expectedUuid)

        assertNotNull(playerProfile)
        assertEquals(playerName, playerProfile?.name)
        assertEquals(expectedUuidStr, playerProfile?.id)
    }
}
