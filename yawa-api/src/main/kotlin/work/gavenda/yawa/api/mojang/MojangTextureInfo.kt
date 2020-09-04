package work.gavenda.yawa.api.mojang

import java.util.*

/**
 * Represents a Mojang Texture Info from a decoded base64 json string.
 */
data class MojangTextureInfo(
    val timestamp: Long,
    val profileName: String,
    val profileId: UUID,
    val textures: Map<MojangTextureType, MojangTexture>
)

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

