import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

shadowedKotlinProject("YawaAPI")
paperPlugin()

dependencies {
    compileOnly(Library.PROTOCOL_LIB)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}

val shadowJar by tasks.existing(ShadowJar::class)
val test by tasks.existing(Test::class)

test {
    useJUnitPlatform()
}

shadowJar {
    dependencies {
        relocate("com.comphenix.packetwrapper", "work.gavenda.packetwrapper")
    }
}