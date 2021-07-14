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

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*
import org.gradle.language.jvm.tasks.ProcessResources

/**
 * Configures the current project as a Paper plugin.
 */
@Suppress("UnstableApiUsage")
fun Project.paperPlugin() {
    val shadowJar by tasks.existing(ShadowJar::class)
    val processResources by tasks.existing(ProcessResources::class)
    val jar by tasks.existing(Jar::class)
    val test by tasks.existing(Test::class)

    dependencies {
        "compileOnly"(Library.PAPER)
        "implementation"(Library.PAPER_LIB)
    }

    jar {
        manifest {
            attributes(
                "Paper-Version" to Version.PAPER
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
        val projectPackage = project.group

        // Exclude dependencies
        dependencies {
            // Remove Kotlin
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk7:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib-common:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-reflect:.*"))
            exclude(dependency("org.jetbrains:annotations:.*"))
            // Remove Kyori
            exclude(dependency("net.kyori:adventure-api:.*"))
            exclude(dependency("net.kyori:adventure-key:.*"))
            exclude(dependency("net.kyori:adventure-nbt:.*"))
            exclude(dependency("net.kyori:adventure-platform-api:.*"))
            exclude(dependency("net.kyori:adventure-platform-bukkit:.*"))
            exclude(dependency("net.kyori:serializer-bungeecord:.*"))
            exclude(dependency("net.kyori:serializer-craftbukkit:.*"))
            exclude(dependency("net.kyori:examination-api:.*"))
            exclude(dependency("net.kyori:examination-string:.*"))
        }

        // Relocate internal libraries
        relocate("io.papermc.lib", "$projectPackage.paperlib")
    }
}