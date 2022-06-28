dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
        create("mcLibs") {
            from(files("../gradle/mcLibs.versions.toml"))
        }
    }
}