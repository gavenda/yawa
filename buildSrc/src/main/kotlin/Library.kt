object Version {
    const val KOTLIN = "1.4.0"
    const val KOTLINX_COROUTINES = "1.3.9"
    const val SPIGOT = "1.16.2-R0.1-SNAPSHOT"
    const val PAPER = "1.16.2-R0.1-SNAPSHOT"
    const val HIKARICP = "3.4.5"
    const val KODEIN = "7.0.0"
    const val PROTOCOL_LIB = "4.5.1"
    const val EXPOSED = "0.27.1"
}

object Library {
    const val KOTLINX_COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.KOTLINX_COROUTINES}"
    const val SPIGOT = "org.spigotmc:spigot-api:${Version.SPIGOT}"
    const val PAPER = "com.destroystokyo.paper:paper-api:${Version.PAPER}"
    const val HIKARICP = "com.zaxxer:HikariCP:${Version.HIKARICP}"
    const val KODEIN = "org.kodein.di:kodein-di:${Version.KODEIN}"
    const val PROTOCOL_LIB = "com.comphenix.protocol:ProtocolLib:${Version.PROTOCOL_LIB}"

    object Exposed {
        const val CORE = "org.jetbrains.exposed:exposed-core:${Version.EXPOSED}"
        const val DAO = "org.jetbrains.exposed:exposed-dao:${Version.EXPOSED}"
        const val JDBC = "org.jetbrains.exposed:exposed-jdbc:${Version.EXPOSED}"
    }
}