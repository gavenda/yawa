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

package work.gavenda.yawa.api.mojang

import com.google.gson.annotations.SerializedName

/**
 * Represents a Mojang service.
 */
data class MojangService(
    val name: String,
    val status: MojangServiceStatus
)

/**
 * Represents the status of a Mojang service.
 */
enum class MojangServiceStatus {
    /**
     * Service is running without issues.
     */
    @SerializedName(MOJANG_VAL_GREEN)
    OK,

    /**
     * Service is running with some issues.
     */
    @SerializedName(MOJANG_VAL_YELLOW)
    PARTIAL,

    /**
     * Service is unavailable.
     */
    @SerializedName(MOJANG_VAL_RED)
    UNAVAILABLE;

    companion object {
        /**
         * Parses the given color to an equivalent [MojangServiceStatus].
         * @return [MojangServiceStatus]
         * @throws IllegalArgumentException when given an unknown color
         */
        fun from(color: String): MojangServiceStatus {
            return when (color) {
                MOJANG_VAL_GREEN -> OK
                MOJANG_VAL_YELLOW -> PARTIAL
                MOJANG_VAL_RED -> UNAVAILABLE
                else -> throw IllegalArgumentException("Unknown service status: $color")
            }
        }
    }
}