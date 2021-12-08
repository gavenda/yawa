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

package work.gavenda.yawa.api.placeholder

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.template.TemplateResolver
import org.bukkit.World
import org.bukkit.entity.Player
import work.gavenda.yawa.api.YawaAPI
import work.gavenda.yawa.api.apiLogger

/**
 * Simple placeholder API.
 */
object Placeholder {

    private val providers = mutableSetOf<PlaceholderProvider>()

    /**
     * Register a placeholder provider.
     */
    fun register(provider: PlaceholderProvider) {
        apiLogger.info("Registered placeholder: ${provider::class.qualifiedName}")
        providers.add(provider)
    }

    /**
     * Unregister a placeholder provider.
     */
    fun unregister(provider: PlaceholderProvider) {
        providers.remove(provider)
        apiLogger.info("Unregistered placeholder: ${provider::class.qualifiedName}")
    }

    /**
     * Returns a provider with the given context.
     */
    fun withContext(player: Player, world: World) = PlaceholderContext(providers, player, world)

    /**
     * Returns a provider with the given context.
     */
    fun withContext(player: Player) = PlaceholderContext(providers, player, player.world)

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
    fun parse(text: String, params: Map<String, Any?> = mapOf()): Component {
        val resolver = TemplateResolver.pairs(mergeProviders(params))

        return YawaAPI.MiniMessage
            .deserialize(text, resolver)
    }

    /**
     * Returns this placeholder as a help list.
     */
    fun asHelpList(): List<Component> {
        return mergeProviders().map { entry ->
            val placeholder = entry.key
            val value = entry.value
            val componentText = Component.text("[", NamedTextColor.GREEN)
                .append(Component.text(placeholder))
                .append(Component.text("]", NamedTextColor.GREEN))
                .append(Component.text(" » ", NamedTextColor.YELLOW))

            when (value) {
                is String -> componentText.append(Component.text(value, NamedTextColor.WHITE))
                is Component -> componentText.append(value)
                else -> componentText.append(Component.text("None", NamedTextColor.WHITE))
            }
        }
    }

    private fun mergeProviders(params: Map<String, Any?> = mapOf()): Map<String, Any?> {
        val stringPlaceholders = providers
            .map { it.provideString(player, world) }
            .flatMap { it.entries }
            .associate { it.key to it.value }
            .filter { entry -> entry.value != null }
        val componentPlaceholders = providers
            .map { it.provide(player, world) }
            .flatMap { it.entries }
            .associate { it.key to it.value }
            .filter { entry -> entry.value != null }

        // Order of priority -> params > component > string
        return mapOf(
            *stringPlaceholders.toList().toTypedArray(),
            *componentPlaceholders.toList().toTypedArray(),
            *params.toList().toTypedArray()
        )
    }

}

/**
 * A simple interface for a class that provides placeholders.
 */
interface PlaceholderProvider {
    /**
     * Provide a placeholder.
     */
    fun provide(player: Player?, world: World?): Map<String, Component?> {
        return mapOf()
    }

    /**
     * Provide a placeholder.
     */
    fun provideString(player: Player?, world: World?): Map<String, String?> {
        return mapOf()
    }
}