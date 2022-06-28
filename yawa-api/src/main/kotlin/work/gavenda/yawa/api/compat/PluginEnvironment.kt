package work.gavenda.yawa.api.compat

enum class PluginEnvironment {
    SPIGOT, PAPER
}

val PLUGIN_ENVIRONMENT by lazy {
    try {
        Class.forName("com.destroystokyo.paper.PaperConfig")
        PluginEnvironment.PAPER
    } catch (e: ClassNotFoundException) {
        PluginEnvironment.SPIGOT
    }
}