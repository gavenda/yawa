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

import org.bukkit.World
import org.bukkit.entity.Player

/**
 * Simple placeholder API.
 */
object Placeholder {

    private val providers = mutableSetOf<PlaceholderProvider>()

    /**
     * Register a placeholder provider.
     */
    fun register(provider: PlaceholderProvider) {
        apiLogger.info("Registered placeholder provider: ${provider::class.qualifiedName}")
        providers.add(provider)
    }

    /**
     * Returns a provider with the given context.
     */
    fun withContext(player: Player, world: World) = PlaceholderContext(providers, player, world)

    /**
     * Returns a provider with the given context.
     */
    fun withContext(player: Player) = PlaceholderContext(providers, player = player)

    /**
     * Returns a provider with the given context.
     */
    fun withContext(world: World) = PlaceholderContext(providers, world = world)

    /**
     * Clears all registered providers.
     */
    fun clear() {
        providers.clear()
    }
}

/**
 * Represents a placeholder context.
 */
class PlaceholderContext(
    private val providers: Set<PlaceholderProvider>,
    private val player: Player? = null,
    private val world: World? = null
) {

    /**
     * Parses the text with the appropriate registered placeholders.
     * @param text text to parse
     */
    fun parse(text: String): String {
        val placeholders = providers
            .map { it.provide(player, world) }
            .flatMap { it.entries }
            .map { it.key to it.value }
            .toMap()

        var parsed = text

        placeholders.forEach { entry ->
            val placeholder = entry.key
            val value = entry.value
            if (value != null) {
                parsed = parsed.replace("[${placeholder}]", value)
            }
        }

        return parsed
    }

    fun asHelpList(): List<String> {
        val placeholders = providers
            .map { it.provide(player, world) }
            .flatMap { it.entries }
            .map { it.key to it.value }
            .toMap()

        return placeholders.map { entry ->
            val placeholder = entry.key
            val value = entry.value

            "&a[&r$placeholder&a]&r &e» &r$value"
        }
    }

}

/**
 * A simple interface for a class that provides placeholders.
 */
interface PlaceholderProvider {
    /**
     * Provide a placeholder.
     */
    fun provide(player: Player?, world: World?): Map<String, String?>
}