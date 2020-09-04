package work.gavenda.yawa.api.mojang

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
enum class MojangServiceStatus(val color: String) {
    /**
     * Service is running without issues.
     */
    OK(MOJANG_VAL_GREEN),

    /**
     * Service is running with some issues.
     */
    PARTIAL(MOJANG_VAL_YELLOW),

    /**
     * Service is unavailable.
     */
    UNAVAILABLE(MOJANG_VAL_RED);

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