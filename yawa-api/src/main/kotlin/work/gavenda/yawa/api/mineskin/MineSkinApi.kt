package work.gavenda.yawa.api.mineskin

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import work.gavenda.yawa.api.asHttpConnection
import work.gavenda.yawa.api.asText
import work.gavenda.yawa.api.apiLogger
import java.net.URI
import java.net.URL
import java.net.URLEncoder

/**
 * Provides a simplistic way to access the MineSkin API.
 */
object MineSkinApi {

    private const val URI_API_GENERATE_TEXTURE = "https://api.mineskin.org/generate/url"

    // We could have used a full blown object mapper such as Jackson, but that would be overkill
    private val gson = Gson()

    /**
     * Generates a minecraft texture based on the given url.
     */
    fun generateTexture(url: URI, slim: Boolean = false): MineSkinTexture {
        val httpConnection = URL(URI_API_GENERATE_TEXTURE).asHttpConnection().apply {
            doOutput = true
            requestMethod = "POST"

            val charset = Charsets.UTF_8
            val charsetStr = Charsets.UTF_8.toString()
            val postParams = mapOf(
                MINESKIN_KEY_MODEL to if (slim) "slim" else "",
                MINESKIN_KEY_URL to url.toString()
            )
            val postData = StringBuilder()

            postParams.forEach {
                if (postData.isNotEmpty()) {
                    postData.append('&')
                }
                postData.append(URLEncoder.encode(it.key, charsetStr))
                postData.append('=')
                postData.append(URLEncoder.encode(it.value, charsetStr))
            }

            val postDataBytes = postData.toString().toByteArray(charset)

            // Headers
            addRequestProperty("Accept", "application/json")
            addRequestProperty("User-Agent", "YawaAPI")
            addRequestProperty("Charset", "UTF-8")
            addRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            addRequestProperty("Content-Length", postDataBytes.size.toString())

            // Write body
            outputStream.write(postDataBytes)
        }

        val response = httpConnection.asText()

        try {
            val result = gson.fromJson(response, MineSkinResult::class.java)
            return result.data.texture
        } catch (e: JsonSyntaxException) {
            apiLogger.error("Unable to generate texture", e)
            throw e
        }
    }

}