plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")
    implementation(kotlin(module = "serialization", version = "1.7.0"))
}

repositories {
    gradlePluginPortal()
}