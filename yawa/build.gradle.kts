plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
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
    compileOnly(mcLibs.paper.api)
    compileOnly(mcLibs.bundles.adventure)
    compileOnly(project(":yawa-api", "shadow"))
    compileOnly(mcLibs.vault)
}
