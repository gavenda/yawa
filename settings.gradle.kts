// What we all are
rootProject.name = "yawa"

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        mavenCentral()
        maven(url = "https://papermc.io/repo/repository/maven-public") {
            name = "paper-snapshots"
        }
        maven(url = "https://repo.dmulloy2.net/repository/public/") {
            name = "dmulloy2-repo"
        }
        maven(url = "https://nexus.scarsz.me/content/groups/public/") {
            name = "scarsz"
        }
        maven(url = "https://jitpack.io") {
            name = "jitpack"
        }
    }

    versionCatalogs {
        create("mcLibs") {
            from(files("gradle/mcLibs.versions.toml"))
        }
    }
}