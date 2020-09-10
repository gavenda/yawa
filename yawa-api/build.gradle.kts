import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

shadowedKotlinProject("YawaAPI")
paperPlugin()

dependencies {
    compileOnly(Library.PROTOCOL_LIB)
}

tasks.shadowJar {
    dependencies {
        relocate("com.comphenix.packetwrapper", "work.gavenda.packetwrapper")
    }
}