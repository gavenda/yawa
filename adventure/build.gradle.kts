plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

kotlinProject("Adventure")
deployablePlugin()

version = Version.ADVENTURE

dependencies {
    implementation(libs.bundles.adventure)
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
        dependencies {
            // Remove Kotlin
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk7:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib-common:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-reflect:.*"))
            exclude(dependency("org.jetbrains:annotations:.*"))
        }

        archiveFileName.set(jar.get().archiveFileName)
        mustRunAfter(copyLicense)
    }

    build {
        dependsOn(shadowJar)
    }
}
