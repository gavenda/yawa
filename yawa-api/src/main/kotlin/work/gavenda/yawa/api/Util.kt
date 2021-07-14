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

package work.gavenda.yawa.api

import java.net.HttpURLConnection
import java.net.URL

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

