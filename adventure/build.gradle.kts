plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

kotlinProject("Adventure")
deployablePlugin()

version = libs.versions.adventure

dependencies {
    implementation(libs.bundles.adventure)

    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly(libs.spigot)
}

tasks {
    val copyLicense = named("copyLicense")

    jar {
        manifest {
            attributes(
                "Spigot-Version" to libs.versions.spigot
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
        mustRunAfter(copyLicense)
    }
}
