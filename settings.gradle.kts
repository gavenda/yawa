// What we all are
rootProject.name = "yawa"

enableFeaturePreview("VERSION_CATALOGS")

include("kotlin")
include("yawa")
include("yawa-api")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            // Versions
            version("paper", "1.18-R0.1-SNAPSHOT")

            // Libraries
            alias("paper").to("io.papermc.paper", "paper-api").versionRef("paper")
            alias("paper-lib").to("io.papermc:paperlib:1.0.7")
            alias("spigot").to("org.spigotmc:spigot-api:1.18-R0.1-SNAPSHOT")
            alias("protocol-lib").to("com.comphenix.protocol:ProtocolLib:4.8.0-SNAPSHOT")
            alias("vault").to("com.github.MilkBowl:VaultAPI:1.7")
            alias("hikari").to("com.zaxxer:HikariCP:5.0.0")
            alias("exposed-core").to("org.jetbrains.exposed:exposed-core:0.36.2")
            alias("exposed-dao").to("org.jetbrains.exposed:exposed-dao:0.36.2")
            alias("exposed-jdbc").to("org.jetbrains.exposed:exposed-jdbc:0.36.2")
            alias("log4j2").to("org.apache.logging.log4j:log4j-api:2.14.1")
            alias("discordsrv").to("com.discordsrv:discordsrv:1.24.0")
            alias("kyori-adventurelib").to("net.kyori:adventure-platform-bukkit:4.0.1")
            alias("kyori-minimessage").to("net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT")
            // Bundles
            bundle("exposed", listOf("exposed-core", "exposed-dao", "exposed-jdbc"))
            bundle("paper", listOf("paper", "paper-lib"))
        }
    }
}