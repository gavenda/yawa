// What we all are
rootProject.name = "yawa"

include("yawa")
include("yawa-api")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        mavenCentral()
        maven(url = "https://papermc.io/repo/repository/maven-public") {
            name = "paper-snapshots"
        }
        maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots") {
            name = "spigot-snapshots"
        }
        maven(url = "https://repo.dmulloy2.net/nexus/repository/public") {
            name = "protocollib-snapshots"
        }
        maven(url = "https://m2.dv8tion.net/releases") {
            name = "jda-releases"
        }
        maven(url = "https://jitpack.io") {
            name = "jitpack"
        }
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/") {
            name = "sonatype-oss-snapshots"
        }
    }

    versionCatalogs {
        create("libs") {
            // Versions
            version("kotlin", "1.7.0")
            version("paper", "1.19-R0.1-SNAPSHOT")
            version("spigot", "1.19-R0.1-SNAPSHOT")
            version("adventure", "4.11.0")
            version("adventure-bungeecord", "4.1.0")
            version("exposed", "0.38.2")
            version("discord", "5.0.0-alpha.12")
            version("discord-webhooks", "0.8.0")

            // Discord
            library("discord", "net.dv8tion", "JDA").versionRef("discord")
            library("discord-webhooks", "club.minnced", "discord-webhooks").versionRef("discord-webhooks")

            // Libraries
            library("paper-api", "io.papermc.paper", "paper-api").versionRef("paper")
            library("paper-lib", "io.papermc:paperlib:1.0.7")
            library("spigot", "org.spigotmc", "spigot").versionRef("spigot")
            library("spigot-api", "org.spigotmc", "spigot-api").versionRef("spigot")
            library("protocol-lib", "com.comphenix.protocol:ProtocolLib:5.0.0-SNAPSHOT")
            library("vault", "com.github.MilkBowl:VaultAPI:1.7")
            library("hikari", "com.zaxxer:HikariCP:5.0.1")
            library("exposed-core", "org.jetbrains.exposed", "exposed-core").versionRef("exposed")
            library("exposed-dao", "org.jetbrains.exposed", "exposed-dao").versionRef("exposed")
            library("exposed-jdbc", "org.jetbrains.exposed", "exposed-jdbc").versionRef("exposed")
            library("log4j2", "org.apache.logging.log4j:log4j-api:2.17.2")
            library("discordsrv", "com.discordsrv:discordsrv:1.25.1")
            library("adventure-platform-bukkit", "net.kyori:adventure-platform-bukkit:4.1.0")
            library("adventure-api", "net.kyori", "adventure-api").versionRef("adventure")
            library("adventure-key", "net.kyori", "adventure-key").versionRef("adventure")
            library("adventure-nbt", "net.kyori", "adventure-nbt").versionRef("adventure")
            library("adventure-text-minimessage", "net.kyori", "adventure-text-minimessage").versionRef("adventure")
            library("adventure-text-serializer-plain", "net.kyori", "adventure-text-serializer-plain").versionRef("adventure")
            library("adventure-text-serializer-legacy", "net.kyori", "adventure-text-serializer-legacy").versionRef("adventure")
            library("adventure-text-serializer-bungeecord", "net.kyori", "adventure-text-serializer-bungeecord").versionRef("adventure-bungeecord")
            library("adventure-text-serializer-gson", "net.kyori", "adventure-text-serializer-gson").versionRef("adventure")
            // Bundles
            bundle("exposed", listOf("exposed-core", "exposed-dao", "exposed-jdbc"))
            bundle("spigot", listOf("spigot"))
            bundle("adventure", listOf(
                "adventure-api",
                "adventure-key",
                "adventure-nbt",
                "adventure-text-minimessage",
                "adventure-text-serializer-plain",
                "adventure-text-serializer-legacy",
                "adventure-text-serializer-bungeecord",
                "adventure-text-serializer-gson",
                "adventure-platform-bukkit"
            ))
        }
    }
}