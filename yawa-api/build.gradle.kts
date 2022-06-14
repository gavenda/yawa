plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shadow)
}

minecraftPlugin(name = "YawaAPI")

dependencies {
    compileOnly(libs.kotlin.stdlib.jdk8)
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.log4j2)

    compileOnly(mcLibs.paper.api)
    compileOnly(mcLibs.protocol.lib)
    compileOnly(mcLibs.bundles.adventure)
}
