plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("gradle.plugin.com.github.jengelman.gradle.plugins:shadow:7.0.0")
}

repositories {
    gradlePluginPortal()
}