import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val shadowJar by tasks.existing(ShadowJar::class)

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

kotlinProject("YawaAPI")
spigotPlugin()

dependencies {
    compileOnly(Library.PROTOCOL_LIB)
}

shadowJar {
    relocate("net.kyori", "work.gavenda.yawa.api.lib.kyori")
}
