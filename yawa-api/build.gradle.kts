plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

kotlinProject("YawaAPI")
deployablePlugin()

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))

    // API
    compileOnly(libs.spigot)

    // Dependencies
    compileOnly(libs.protocol.lib)
    compileOnly(libs.bundles.adventure)
    implementation(libs.paper.lib)
    implementation(libs.log4j2)
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

        dependencies {
            // Remove Kotlin
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk7:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib-common:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-reflect:.*"))
            exclude(dependency("org.jetbrains:annotations:.*"))
        }

        relocate("io.papermc.lib", "work.gavenda.yawa.api.paperlib")

        mustRunAfter(copyLicense)
    }

    build {
        dependsOn(shadowJar)
    }
}
