package work.gavenda.yawa.api.mineskin

import java.util.*

data class MineSkinResult(
    val id: Int,
    val name: String,
    val data: MineSkinTextureData,
    val duration: Int,
    val accountId: String,
    val private: Boolean,
    val views: Int,
    val nextRequest: Int
)

data class MineSkinTextureData(
    val uuid: UUID,
    val texture: MineSkinTexture
)

data class MineSkinTexture(
    val value: String,
    val signature: String,
    val url: String
)