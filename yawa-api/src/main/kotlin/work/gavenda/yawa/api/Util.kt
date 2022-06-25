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

import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
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

@Suppress("DEPRECATION")
fun Array<net.md_5.bungee.api.chat.BaseComponent>.toComponent(): Component {
    return BungeeComponentSerializer.get().deserialize(this)
}

@Suppress("DEPRECATION")
fun Component.toBaseComponent(): Array<net.md_5.bungee.api.chat.BaseComponent> {
    return BungeeComponentSerializer.get().serialize(this)
}

fun Component.toPlainText(): String {
    return PlainTextComponentSerializer.plainText().serialize(this)
}

fun Component.toLegacyText(): String {
    return BukkitComponentSerializer.legacy().serialize(this)
}

fun String.toComponent(): Component {
    return BukkitComponentSerializer.legacy().deserialize(this)
}

fun String.capitalizeFully(): String {
    if (isEmpty() || isBlank()) {
        return ""
    }

    if (length == 1) {
        return Character.toUpperCase(this[0]).toString()
    }

    val textArray = split(" ")
    val stringBuilder = StringBuilder()

    for ((index, item) in textArray.withIndex()) {
        // If item is empty string, continue to next item
        if (item.isEmpty()) {
            continue
        }

        stringBuilder
            .append(Character.toUpperCase(item[0]))

        // If the item has only one character then continue to next item because we have already capitalized it.
        if (item.length == 1) {
            continue
        }

        for (i in 1 until item.length) {
            stringBuilder
                .append(Character.toLowerCase(item[i]))
        }

        if (index < textArray.lastIndex) {
            stringBuilder
                .append(" ")
        }
    }

    return stringBuilder.toString()
}