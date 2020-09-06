package work.gavenda.yawa.api.mineskin

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.net.URI

class MineSkinApiTest {

    @Test
    @DisplayName("Test MineSkin API")
    fun testMineSkinApi() {
        val url = URI("https://www.minecraftskins.com/uploads/skins/2020/09/06/-cute-girl---recolor-contest-entry--15222439.png")
        assertDoesNotThrow {
            MineSkinApi.generateTexture(url, true)
        }
    }

}