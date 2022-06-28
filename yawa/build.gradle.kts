plugins {
    kotlin("jvm")
}

minecraftPlugin(name = "Yawa")

dependencies {
    compileOnly(libs.kotlin.stdlib.jdk8)

    compileOnly(libs.discord) {
        exclude(group = "club.minnced", module = "opus-java")
    }
    compileOnly(libs.discord.webhooks)
    compileOnly(libs.hikari)
    compileOnly(libs.bundles.exposed) {
        isTransitive = false
    }
    compileOnly(libs.log4j2)

    compileOnly(mcLibs.protocol.lib)
    compileOnly(mcLibs.datafixerupper)
    compileOnly(mcLibs.paper.api)
    compileOnly(mcLibs.bundles.adventure)
    compileOnly(project(path = ":yawa-api"))
    compileOnly(mcLibs.vault)
}
