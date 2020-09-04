package work.gavenda.yawa.api.mojang

import com.google.gson.Gson
import java.util.*

/**
 * Represents a Mojang Texture Info from a decoded base64 json string.
 */
data class MojangTextureInfo(
    val timestamp: Long,
    val profileName: String,
    val profileId: UUID,
    val textures: Map<MojangTextureType, MojangTexture>
) {

    /**
     * Returns this texture represented as JSON string.
     */
    val asJson: String get() {
        return Gson().toJson(this)
    }

    /**
     * Returns this texture as a base64 encoded JSON string.
     */
    val asJsonBase64: String get() {
        return Base64.getEncoder().encodeToString(asJson.toByteArray(Charsets.UTF_8))
    }

}

/**
 * Represents a Mojang Texture Type
 */
enum class MojangTextureType {
    CAPE,
    SKIN
}

/**
 * Represents a Mojang texture.
 */
data class MojangTexture(
    val url: String,
    val model: MojangSkinModel = MojangSkinModel.STEVE
)

/**
 * Represents a mojang skin model.
 */
enum class MojangSkinModel {
    /**
     * The slim skin model.
     */
    ALEX,

    /**
     * The normal skin model.
     */
    STEVE
}

