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
    val id: String,
    val name: String,
    val properties: List<MojangProfileProperty> = listOf()
)

/**
 * Represents a Mojang profile property.
 */
data class MojangProfileProperty(
    val name: String,
    val value: String,
    val signature: String
)