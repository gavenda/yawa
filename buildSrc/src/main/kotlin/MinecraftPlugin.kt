/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (c) 2022 Gavenda <gavenda@disroot.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.accessors.dm.LibrariesForMcLibs
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.attributes
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.gradle.language.jvm.tasks.ProcessResources

/**
 * Configures the project as a Minecraft plugin.
 */
fun Project.minecraftPlugin(name: String) {
    val libs = the<LibrariesForLibs>()
    val mcLibs = the<LibrariesForMcLibs>()

    tasks.register<Copy>("copyLicense") {
        val srcDir = file("$rootDir/LICENSE")
        val distDir = file("$buildDir/resources/main/META-INF/")

        from(srcDir)
        into(distDir)
    }

    tasks.named("processResources", ProcessResources::class) {
        filesMatching("plugin.yml") {
            expand(project.properties)
        }
    }

    tasks.register<Copy>("package") {
        group = "distribution"
        description = "Publish minecraft plugin to local server testing directory"

        val srcDir = file("$buildDir/libs")
        val distDir = file("$rootDir/yawa-server/plugins")
        from(srcDir)
        into(distDir)

        dependsOn("build")
    }

    tasks.named("jar", Jar::class) {
        archiveFileName.set("$name.${archiveExtension.get()}")

        val sys = System.getProperties().toMap()
        val jdk = "${sys["java.version"]} ${sys["java.vendor"]} ${sys["java.vm.version"]}"
        val os = "${sys["os.name"]} ${sys["os.arch"]} ${sys["os.version"]}"

        manifest {
            attributes(
                "Built-By" to sys["user.name"],
                "Created-By" to "Gradle ${gradle.gradleVersion}",
                "Build-System" to os,
                "Build-Jdk" to jdk,
                "Kotlin-Version" to libs.versions.kotlin.asProvider().get(),
                "Implementation-Version" to project.version,
                "Implementation-Title" to name,
                "Implementation-Vendor" to "Gavenda",
                "Paper-Version" to mcLibs.versions.paper.asProvider().get()
            )
        }

        mustRunAfter("copyLicense")
    }

    tasks.named("build") {
        dependsOn("copyLicense")
    }
}