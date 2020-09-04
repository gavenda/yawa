package work.gavenda.yawa.api.mojang

import com.google.gson.JsonParser
import java.math.BigInteger
import java.util.*

val decoder: Base64.Decoder = Base64.getDecoder()
val parser: JsonParser = JsonParser()

/**
 * Represents a Mojang profile.
 */
data class MojangProfile(
    val uuid: UUID,
    val name: String,
    val properties: List<MojangProfileProperty>
) {

    /**
     * Retrieves the texture information in the listed properties.
     */
    val textureInfo: MojangTextureInfo get() {
        val texture = properties.find { it.name == MOJANG_VAL_TEXTURES }
        val textureJsonStr = decoder.decode(texture?.value).toString(Charsets.UTF_8)
        val textureJson = parser.parse(textureJsonStr).asJsonObject

        val timestamp = textureJson[MOJANG_KEY_TIMESTAMP].asLong
        val profileId = textureJson[MOJANG_KEY_PROFILE_ID].asString.let {
            // Parse to uuid
            val bi1 = BigInteger(it.substring(0, 16), 16)
            val bi2 = BigInteger(it.substring(16, 32), 16)
            UUID(bi1.toLong(), bi2.toLong())
        }
        val profileName = textureJson[MOJANG_KEY_PROFILE_NAME].asString
        val texturesJson = textureJson[MOJANG_KEY_TEXTURES].asJsonObject
        val textures = texturesJson.entrySet().map {
            val textureInfoJson = it.value.asJsonObject
            val model = if(textureInfoJson.has("metadata")) {
                val metadata = textureInfoJson["metadata"].asJsonObject
                if(metadata.has("model")) {
                    MojangSkinModel.ALEX
                } else MojangSkinModel.STEVE
            } else MojangSkinModel.STEVE

            MojangTextureType.valueOf(it.key) to MojangTexture(textureInfoJson[MOJANG_KEY_URL].asString, model)
        }.toMap()

        return MojangTextureInfo(timestamp, profileName, profileId, textures)
    }

}

/**
 * Represents a Mojang profile property.
 */
data class MojangProfileProperty(
    val name: String,
    val value: String,
    val signature: String
)