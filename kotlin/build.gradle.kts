plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

version = Version.KOTLIN

kotlinProject("Kotlin")

// Manually set the server plugin api, since this basically is a provider for Kotlin
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    compileOnly(Library.PAPER)
}

// Also manual process of resources instead of using utilities from buildSrc
tasks.processResources {
    filesMatching("plugin.yml") {
        expand(project.properties)
    }
}

tasks.shadowJar {
    archiveFileName.set("Kotlin.${archiveExtension.get()}")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
