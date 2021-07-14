/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (C) 2020 Gavenda <gavenda@disroot.org>
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

import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*

/**
 * Configures the project as a shadowed Kotlin project.
 */
fun Project.kotlinProject(archiveName: String) {
    val build = tasks.named("build")
    val jar by tasks.existing(Jar::class)
    val sysProp = System.getProperties().toMap()
    val javaVersion = "${sysProp["java.version"]} ${sysProp["java.vendor"]} ${sysProp["java.vm.version"]}"
    val operatingSystem = "${sysProp["os.name"]} ${sysProp["os.arch"]} ${sysProp["os.version"]}"

    jar {
        archiveFileName.set("$archiveName.${archiveExtension.get()}")

        manifest {
            attributes(
                "Built-By" to sysProp["user.name"],
                "Created-By" to "Gradle ${gradle.gradleVersion}",
                "Build-System" to operatingSystem,
                "Build-Jdk" to javaVersion,
                "Kotlin-Version" to Version.KOTLIN,
                "Implementation-Version" to project.version,
                "Implementation-Title" to archiveName,
                "Implementation-Vendor" to "Gavenda"
            )
        }
    }

    dependencies {
        "compileOnly"(kotlin("stdlib-jdk8"))
    }

    val copyLicense = tasks.register<Copy>("copyLicense") {
        val srcDir = file("$rootDir/LICENSE")
        val distDir = file("$buildDir/resources/main/META-INF/")

        from(srcDir)
        into(distDir)
    }

    jar.configure {
        mustRunAfter(copyLicense)
    }

    build {
        dependsOn(copyLicense)
    }
}