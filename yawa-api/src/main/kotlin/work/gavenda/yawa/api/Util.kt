/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (C) 2020 Gavenda <gavenda@disroot.org>
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
 */

package work.gavenda.yawa.api

import org.bukkit.ChatColor
import java.io.File
import java.io.FileInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import javax.xml.bind.annotation.adapters.HexBinaryAdapter

/**
 * Translate this string with colors with a default alternate char code of '&'.
 */
fun String.translateColorCodes(altCharCode: Char = '&'): String {
    return ChatColor.translateAlternateColorCodes(altCharCode, this)
}

/**
 * Returns the response in text of the [URL] using an HTTP GET request.
 */
fun URL.asText(): String {
    return openConnection().run {
        this as HttpURLConnection
        inputStream.bufferedReader().readText()
    }
}

/**
 * Returns the response in text of the [HttpURLConnection].
 */
fun HttpURLConnection.asText(): String {
    return inputStream.bufferedReader().readText()
}

/**
 * Returns this [URL] as an instance of [HttpURLConnection].
 */
fun URL.asHttpConnection(): HttpURLConnection {
    return openConnection() as HttpURLConnection
}

/**
 * Returns the sha-1 hash of this file.
 */
fun File.sha1(): String {
    val digest = MessageDigest.getInstance("SHA-1")
    val fileInputStream = FileInputStream(this)
    var n = 0
    val buffer = ByteArray(8192)
    while (n != -1) {
        n = fileInputStream.read(buffer)
        if (n > 0) digest.update(buffer, 0, n)
    }

    return HexBinaryAdapter().marshal(digest.digest())
}

/**
 * Fixes a URL if it doesn't end with a '/'.
 */
fun String.fixUrl(): String {
    return if (this.isEmpty() || this.endsWith("/")) this else "$this/"
}
