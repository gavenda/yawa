plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

kotlinProject("Yawa")
deployablePlugin()

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly(kotlin("reflect"))

    // Paper API
    compileOnly(libs.bundles.paper)

    // Implement our own api
    compileOnly(project(":yawa-api"))

    // Data source + Db oop
    implementation(libs.hikari)
    implementation(libs.bundles.exposed)

    // Minecraft plugins
    compileOnly(libs.vault)
    compileOnly(libs.protocol.lib)
    compileOnly(libs.discordsrv)
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

        relocate("com.zaxxer.hikari", "work.gavenda.yawa.hikari")
        relocate("io.papermc.lib", "work.gavenda.yawa.paperlib")

        mustRunAfter(copyLicense)
    }

    build {
        dependsOn(shadowJar)
    }
}
