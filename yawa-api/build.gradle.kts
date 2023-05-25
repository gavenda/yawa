plugins {
    id("yawa.kotlin-conventions")
    id("yawa.paper-plugin")
    kotlin("plugin.serialization")
}

dependencies {
    compileOnly(libs.kotlin.stdlib.jdk8)
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.log4j2)

    compileOnly(mcLibs.paper.api)
    compileOnly(mcLibs.nbtapi)
    compileOnly(mcLibs.protocol.lib)
    compileOnly(mcLibs.bundles.adventure)
}
