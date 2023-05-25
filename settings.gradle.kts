// What we all are
rootProject.name = "yawa"

include("yawa")
include("yawa-api")

@Suppress("UnstableApiUsage")
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
        maven(url = "https://libraries.minecraft.net") {
            name = "minecraft"
        }
        maven(url = "https://repo.codemc.org/repository/maven-public/") {
            name = "codemc"
        }
    }

    versionCatalogs {
        create("mcLibs") {
            from(files("gradle/mcLibs.versions.toml"))
        }
    }
}