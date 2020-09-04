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
        logger.info("Registered placeholder provider: ${provider::class.qualifiedName}")
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
            if(value != null) {
                parsed = parsed.replace("[${placeholder}]", value)
            }
        }

        return parsed
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