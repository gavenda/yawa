package work.gavenda.yawa.api

enum class PluginEnvironment {
    BUKKIT, PAPER
}

val pluginEnvironment by lazy {
    try {
        Class.forName("com.destroystokyo.paper.PaperConfig")
        PluginEnvironment.PAPER
    } catch (e: ClassNotFoundException) {
        PluginEnvironment.BUKKIT
    }
}