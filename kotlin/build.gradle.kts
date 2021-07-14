plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

version = Version.KOTLIN

kotlinProject("Kotlin")
shadowedKotlinProject()
deployablePlugin()

// Manually set the server plugin api, since this basically is a provider for Kotlin
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    compileOnly(Library.SPIGOT)
}
