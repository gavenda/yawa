plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

kotlinProject("YawaAPI")
deployablePlugin()

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))

    // Paper API
    compileOnly(libs.bundles.paper)
    implementation(libs.kyori.adventurelib)
    implementation(libs.kyori.minimessage)

    // Dependencies
    compileOnly(libs.protocol.lib)
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

        dependencies {
            // Remove Kotlin
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk7:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib-common:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-reflect:.*"))
            exclude(dependency("org.jetbrains:annotations:.*"))
        }

        relocate("io.papermc", "work.gavenda.yawa.api.paper")

        mustRunAfter(copyLicense)
    }

    build {
        dependsOn(shadowJar)
    }
}
