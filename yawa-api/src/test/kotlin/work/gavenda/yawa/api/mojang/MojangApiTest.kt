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
        val expectedUuid = UUID.fromString("542189cf-68fb-415b-bbe6-c60da626e65a")
        val playerName = "Red"

        val playerUuid = MojangAPI.findUuidByUsername(playerName)

        assertEquals(expectedUuid, playerUuid)

        val playerProfile = MojangAPI.findProfile(expectedUuid)

        println(playerProfile)

        assertNotNull(playerProfile)
        assertEquals(playerName, playerProfile?.name)
        assertEquals(expectedUuid, playerProfile?.uuid)

        val textureInfo = playerProfile?.textureInfo

        println(textureInfo)

        assertEquals(expectedUuid, textureInfo?.profileId)
        assertEquals(playerName, textureInfo?.profileName)

        println(textureInfo?.textures)

        textureInfo?.textures?.forEach { println(it.value) }
    }
}
