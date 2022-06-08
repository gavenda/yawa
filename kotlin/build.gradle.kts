plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

kotlinProject("Kotlin")
deployablePlugin()

version = Version.KOTLIN

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

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

    build {
        dependsOn(shadowJar)
    }
}
