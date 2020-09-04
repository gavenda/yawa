package work.gavenda.yawa.api.mojang

import com.google.gson.JsonParser
import work.gavenda.yawa.api.asText
import work.gavenda.yawa.api.logger
import java.math.BigInteger
import java.net.URL
import java.text.ParseException
import java.util.*

/**
 * Provides a simplistic way to access the Mojang API.
 */
object MojangAPI {

    private const val URI_API_STATUS = "https://status.mojang.com/check"
    private const val URI_API_USERNAME_UUID = "https://api.mojang.com/users/profiles/minecraft"
    private const val URI_API_PROFILE = "https://sessionserver.mojang.com/session/minecraft/profile"

    // We could have used a full blown object mapper such as Jackson, but that would be overkill
    private val parser = JsonParser()

    /**
     * Retrieves all Mojang services.
     * @return a list of [MojangService]
     */
    fun findServiceStatus(): List<MojangService> {
        val response = URL(URI_API_STATUS).asText()

        try {
            val element = parser.parse(response)
            val array = element.asJsonArray

            return array.map {
                val obj = it.asJsonObject.entrySet().first()
                val name = obj.key
                val status = MojangServiceStatus.from(obj.value.asString)

                MojangService(name, status)
            }
        } catch (e: ParseException) {
            logger.error("Unable to retrieve service status: ${e.message}")
        }

        return emptyList()
    }

    /**
     * Retrieves Minecraft UUID given the username.
     * @param username minecraft username
     * @return an instance of [UUID], will return null when not found
     */
    fun findUuidByUsername(username: String): UUID? {
        val response = URL("$URI_API_USERNAME_UUID/$username").asText()

        try {
            val element = parser.parse(response)
            val obj = element.asJsonObject
            val uuid = obj[MOJANG_KEY_ID].asString

            // Parse to uuid
            val bi1 = BigInteger(uuid.substring(0, 16), 16)
            val bi2 = BigInteger(uuid.substring(16, 32), 16)

            return UUID(bi1.toLong(), bi2.toLong())
        } catch (e: ParseException) {
            logger.error("Unable to retrieve minecraft uuid: ${e.message}")
        }

        return null
    }

    /**
     * Retrieves the Mojang profile of the given user uuid.
     * @param uuid minecraft user uuid
     * @return an instance of [MojangProfile], will return null when not found
     */
    fun findProfile(uuid: UUID): MojangProfile? {
        val uuidNoDash = uuid.toString().replace("-", "")
        val response = URL("$URI_API_PROFILE/$uuidNoDash?unsigned=false").asText()

        try {
            val element = parser.parse(response)
            val obj = element.asJsonObject

            val uuidStr = obj[MOJANG_KEY_ID].asString
            val playerName = obj[MOJANG_KEY_NAME].asString
            val jsonProperties = obj[MOJANG_KEY_PROPERTIES].asJsonArray
            val uuidObj = uuidStr.let {
                // Parse to uuid
                val bi1 = BigInteger(it.substring(0, 16), 16)
                val bi2 = BigInteger(it.substring(16, 32), 16)
                UUID(bi1.toLong(), bi2.toLong())
            }
            val properties = jsonProperties.map {
                val propObj = it.asJsonObject
                val propName = propObj[MOJANG_KEY_NAME].asString
                val propValue = propObj[MOJANG_KEY_VALUE].asString
                val signature = if (propObj.has(MOJANG_KEY_SIGNATURE)) {
                    propObj[MOJANG_KEY_SIGNATURE].asString
                } else ""

                MojangProfileProperty(propName, propValue, signature)
            }

            return MojangProfile(uuidObj, playerName, properties)
        } catch (e: ParseException) {
            logger.error("Unable to retrieve minecraft uuid: ${e.message}")
        }

        return null
    }

}