/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (c) 2022-2023 Gavenda <gavenda@disroot.org>
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

package work.gavenda.yawa.api.placeholder

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.World
import org.bukkit.entity.Player
import work.gavenda.yawa.api.compat.schedulerCompat
import work.gavenda.yawa.logger
import work.gavenda.yawa.plugin
import work.gavenda.yawa.scheduler
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * Simple placeholder API.
 */
object Placeholders {

    private val providers = mutableSetOf<PlaceholderProvider>()
    private val serializer = miniMessage()

    /**
     * Call deserialize directly without context.
     */
    fun deserialize(text: String, vararg tagResolvers: TagResolver) = serializer.deserialize(text, *tagResolvers)

    /**
     * Register a placeholder provider.
     */
    fun register(provider: PlaceholderProvider) {
        logger.info("Registered placeholder: ${provider::class.qualifiedName}")
        providers.add(provider)
    }

    /**
     * Unregister a placeholder provider.
     */
    fun unregister(provider: PlaceholderProvider) {
        providers.remove(provider)
        logger.info("Unregistered placeholder: ${provider::class.qualifiedName}")
    }

    fun noContext() = PlaceholderContext(providers)

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
        val providers = mergeWithProviders(params).map { (placeholder, value) ->
            when (value) {
                is Int -> {
                    Placeholder.unparsed(placeholder, value.toString())
                }

                is String -> {
                    Placeholder.unparsed(placeholder, value)
                }

                is Component -> {
                    Placeholder.component(placeholder, value)
                }

                else -> {
                    TagResolver.empty()
                }
            }
        }

        return Placeholders.deserialize(text, *providers.toTypedArray())
    }

    /**
     * Returns this placeholder as a help list.
     */
    fun asHelpList(): List<Component> {
        return mergeWithProviders().map { (placeholder, value) ->
            return@map Component.textOfChildren(
                Component.text("[", NamedTextColor.GREEN),
                Component.text(placeholder, NamedTextColor.WHITE),
                Component.text("]", NamedTextColor.GREEN),
                Component.text(" » ", NamedTextColor.YELLOW),
                when (value) {
                    is String -> Component.text(value, NamedTextColor.WHITE)
                    is Component -> value
                    else -> Component.text("None", NamedTextColor.WHITE)
                } as ComponentLike
            )
        }
    }

    private fun provideWorld(): Map<String, Component?> {
        return providers
            .map { it.provideWorld(world) }
            .flatMap { it.entries }
            .associate { it.key to it.value }
            .filter { it.value != null }
    }

    private fun provideWorldString(): Map<String, String?> {
        return providers
            .map { it.provideWorldString(world) }
            .flatMap { it.entries }
            .associate { it.key to it.value }
            .filter { it.value != null }
    }

    private fun providePlayer(): Map<String, Component?> {
        return providers
            .map { it.providePlayer(player) }
            .flatMap { it.entries }
            .associate { it.key to it.value }
            .filter { it.value != null }
    }

    private fun providePlayerString(): Map<String, String?> {
        return providers
            .map { it.providePlayerString(player) }
            .flatMap { it.entries }
            .associate { it.key to it.value }
            .filter { it.value != null }
    }

    private fun providers(): Map<String, Any?> {
        val playerStringPlaceholders = CompletableFuture<Map<String, String?>>()
        val playerPlaceholders = CompletableFuture<Map<String, Component?>>()
        val worldStringPlaceholders = CompletableFuture<Map<String, String?>>()
        val worldPlaceholders = CompletableFuture<Map<String, Component?>>()

        if (player != null) {
            player.schedulerCompat.runNow(plugin) {
                playerPlaceholders.complete(providePlayer())
                playerStringPlaceholders.complete(providePlayerString())
            }
        } else {
            playerPlaceholders.complete(emptyMap())
            playerStringPlaceholders.complete(emptyMap())
        }

        if (world != null) {
            scheduler.runNow(plugin) {
                worldPlaceholders.complete(provideWorld())
                worldStringPlaceholders.complete(provideWorldString())
            }
        } else {
            worldPlaceholders.complete(emptyMap())
            worldStringPlaceholders.complete(emptyMap())
        }

        return mapOf(
            *worldStringPlaceholders.get().toList().toTypedArray(),
            *worldPlaceholders.get().toList().toTypedArray(),
            *playerStringPlaceholders.get().toList().toTypedArray(),
            *playerPlaceholders.get().toList().toTypedArray(),
        )
    }

    private fun mergeWithProviders(params: Map<String, Any?> = mapOf()): Map<String, Any?> {
        // Order of priority -> params > component > string
        return mapOf(
            *providers().toList().toTypedArray(),
            *params.toList().toTypedArray()
        )
    }

}

/**
 * A simple interface for a class that provides placeholders.
 */
interface PlaceholderProvider {
    fun providePlayer(player: Player?): Map<String, Component?> {
        return mapOf()
    }

    fun provideWorld(world: World?): Map<String, Component?> {
        return mapOf()
    }

    fun providePlayerString(player: Player?): Map<String, String?> {
        return mapOf()
    }

    fun provideWorldString(world: World?): Map<String, String?> {
        return mapOf()
    }
}