object Version {
    const val KOTLIN = "1.4.0"
    const val SPIGOT = "1.16.2-R0.1-SNAPSHOT"
    const val PAPER = "1.16.2-R0.1-SNAPSHOT"
    const val SQLITE = "3.32.3"
    const val HIKARICP = "3.4.5"
    const val KODEIN = "7.0.0"
    const val PROTOCOL_LIB = "4.5.1"
}

object Library {
    const val SPIGOT = "org.spigotmc:spigot-api:${Version.SPIGOT}"
    const val PAPER = "com.destroystokyo.paper:paper-api:${Version.PAPER}"
    const val SQLITE = "org.xerial:sqlite-jdbc:${Version.SQLITE}"
    const val HIKARICP = "com.zaxxer:HikariCP:${Version.HIKARICP}"
    const val KODEIN = "org.kodein.di:kodein-di:${Version.KODEIN}"
    const val PROTOCOL_LIB = "com.comphenix.protocol:ProtocolLib:${Version.PROTOCOL_LIB}"
}