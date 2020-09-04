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

    @Test
    @DisplayName("Test Serialize Custom Skin and Cape")
    fun testSerialize() {
        val timestamp = System.currentTimeMillis()
        val uuid = UUID.fromString("1ef7a628-886b-49c3-b73b-e566ac5f4b22")
        val name = "Gavenda"

        val textures = mapOf(
            MojangTextureType.CAPE to MojangTexture("http://textures.minecraft.net/texture/c1ff8305460b0a713fa1568d8d650d2def0ce9a41d4544dab9c2fcbd33ad3a00"),
            MojangTextureType.SKIN to MojangTexture("http://textures.minecraft.net/texture/c1ff8305460b0a713fa1568d8d650d2def0ce9a41d4544dab9c2fcbd33ad3a00")
        )

        val textureInfo = MojangTextureInfo(timestamp, name, uuid, textures)


        println(textureInfo.asJson)
    }
}
