import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.io.ByteArrayOutputStream

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

val shadowJar by tasks.existing(ShadowJar::class)

shadowedKotlinProject("Yawa")
paperPlugin()

dependencies {
    compileOnly(project(":yawa-api"))
}