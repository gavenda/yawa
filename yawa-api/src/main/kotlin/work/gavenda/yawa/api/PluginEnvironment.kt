package work.gavenda.yawa.api

enum class PluginEnvironment {
    SPIGOT, PAPER
}

val pluginEnvironment by lazy {
    try {
        Class.forName("com.destroystokyo.paper.PaperConfig")
        PluginEnvironment.PAPER
    } catch (e: ClassNotFoundException) {
        PluginEnvironment.SPIGOT
    }
}