import java.io.ByteArrayOutputStream

plugins {
    base
    alias(libs.plugins.kotlin.jvm)
}

val gitHash: String = ByteArrayOutputStream().use { outputStream ->
    project.exec {
        commandLine("git")
        args("rev-parse", "--short", "HEAD")
        standardOutput = outputStream
    }
    outputStream.toString().trim()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

allprojects {
    group = "work.gavenda.yawa"
    version = "1.3.0"
}

tasks.register("version") {
    doLast {
        println(project.version)
    }
}