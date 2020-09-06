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

        val playerUuid = MojangApi.findUuidByUsername(playerName)

        assertEquals(expectedUuid, playerUuid)

        val playerProfile = MojangApi.findProfile(expectedUuid)

        assertNotNull(playerProfile)
        assertEquals(playerName, playerProfile?.name)
        assertEquals(expectedUuidStr, playerProfile?.id)
    }
}
