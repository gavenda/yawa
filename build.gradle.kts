import java.io.ByteArrayOutputStream

plugins {
    id("yawa.kotlin-conventions")
    id("yawa.paper-plugin")
    kotlin("plugin.serialization")
}

val gitHash: String = ByteArrayOutputStream().use { outputStream ->
    project.exec {
        commandLine("git")
        args("rev-parse", "--short", "HEAD")
        standardOutput = outputStream
    }
    outputStream.toString().trim()
}

group = "work.gavenda.yawa"
version = "1.4.2"

dependencies {
    compileOnly(libs.kotlin.stdlib.jdk8)
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.discord) {
        exclude(group = "club.minnced", module = "opus-java")
    }
    compileOnly(libs.discord.webhooks)
    compileOnly(libs.hikari)
    compileOnly(libs.bundles.exposed) {
        isTransitive = false
    }

    // Minecraft Libraries
    compileOnly(mcLibs.protocol.lib)
    compileOnly(mcLibs.folia.api)
    compileOnly(mcLibs.vault)
}

tasks.register("version") {
    group = "help"
    description = "Prints project version."

    doLast {
        println(project.version)
    }
}