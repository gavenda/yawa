plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

minecraftPlugin(name = "YawaAPI")

dependencies {
    compileOnly(libs.kotlin.stdlib.jdk8)
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.log4j2)

    compileOnly(mcLibs.paper.api)
    compileOnly(mcLibs.nbtapi)
    compileOnly(mcLibs.protocol.lib)
    compileOnly(mcLibs.bundles.adventure)
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}