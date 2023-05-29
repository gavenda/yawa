/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (c) 2022 Gavenda <gavenda@disroot.org>
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

package work.gavenda.yawa.api.mineskin

import kotlinx.serialization.Serializable
import java.util.*

/**
 * Represents a mineskin generation result.
 */
@Serializable
data class MineSkinResult(
    val id: Int,
    val idStr: String,
    val uuid: String,
    val hash: String,
    val name: String,
    val model: String,
    val variant: String,
    val data: MineSkinTextureData,
    val timestamp: Long,
    val duration: Int,
    val accountId: Int,
    val account: Int,
    val server: String,
    val private: Boolean,
    val views: Int,
    val duplicate: Boolean,
    val nextRequest: Int
)

/**
 * Represents a mineskin texture data.
 */
@Serializable
data class MineSkinTextureData(
    @Serializable(with = UuidDeserializer::class)
    val uuid: UUID,
    val texture: MineSkinTexture
)

/**
 * Represents a mineskin texture.
 */
@Serializable
data class MineSkinTexture(
    val value: String,
    val signature: String,
    val url: String
)