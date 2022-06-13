plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
}

kotlinProject("Yawa")
deployablePlugin()

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly(libs.protocol.lib)
    compileOnly(libs.paper.api)
    compileOnly(libs.bundles.adventure)
    compileOnly(project(":yawa-api", "shadow"))
    compileOnly(libs.vault)

    compileOnly(libs.discord) {
        exclude(group = "club.minnced", module = "opus-java")
    }
    compileOnly(libs.discord.webhooks)

    compileOnly(libs.hikari)
    compileOnly(libs.bundles.exposed) {
        isTransitive = false
    }
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
        // relocate("com.zaxxer.hikari", "work.gavenda.yawa.hikari")
        relocate("io.papermc.lib", "work.gavenda.yawa.paperlib")
        mustRunAfter(copyLicense)
    }
}
