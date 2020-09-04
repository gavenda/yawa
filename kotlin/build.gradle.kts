plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

version = Version.KOTLIN

shadowedKotlinProject("Kotlin")

// Manually set the server plugin api, since this basically is a provider for Kotlin
dependencies {
    compileOnly(Library.PAPER)
}

// Also manual process of resources instead of using utilities from buildSrc
tasks.processResources {
    filesMatching("plugin.yml") {
        expand(project.properties)
    }
}
