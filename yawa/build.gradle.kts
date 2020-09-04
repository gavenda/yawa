import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

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