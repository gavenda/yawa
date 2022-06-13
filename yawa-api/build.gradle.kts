plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
}

kotlinProject("YawaAPI")
deployablePlugin()

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    compileOnly(libs.paper.api)
    compileOnly(libs.protocol.lib)
    compileOnly(libs.bundles.adventure)

    implementation(libs.paper.lib)
    compileOnly(libs.log4j2)
}

tasks {
    val copyLicense = named("copyLicense")

    jar {
        manifest {
            attributes(
                "Paper-Version" to libs.versions.paper
            )
        }
    }

    test {
        useJUnitPlatform()
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(project.properties)
        }
    }

    shadowJar {
        archiveFileName.set(jar.get().archiveFileName)
        relocate("io.papermc.lib", "work.gavenda.yawa.api.paperlib")
        mustRunAfter(copyLicense)
    }
}
