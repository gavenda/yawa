plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

kotlinProject("YawaAPI")
deployablePlugin()

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))

    // Paper API
    compileOnly(libs.paper)
    implementation(libs.paper.lib)
    implementation(libs.adventure.text.minimessage) {
        isTransitive = false
    }
    compileOnly(libs.bundles.adventure)

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

        relocate("io.papermc.lib", "work.gavenda.yawa.api.paperlib")

        mustRunAfter(copyLicense)
    }

    build {
        dependsOn(shadowJar)
    }
}
