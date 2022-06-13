/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 *  Copyright (C) 2021 Gavenda <gavenda@disroot.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package work.gavenda.yawa.api.mineskin

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import work.gavenda.yawa.api.asHttpConnection
import work.gavenda.yawa.api.asText
import java.net.URI
import java.net.URL
import java.net.URLEncoder

/**
 * Provides a simplistic way to access the MineSkin API.
 */
object MineSkinApi {

    private const val URI_API_GENERATE_TEXTURE = "https://api.mineskin.org/generate/url"
    private val json = Json {
        ignoreUnknownKeys = true
    }

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

        return json.decodeFromString<MineSkinResult>(response).data.texture
    }

}