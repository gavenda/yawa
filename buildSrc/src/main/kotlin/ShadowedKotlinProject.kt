/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 *  Copyright (C) 2021 Gavenda <gavenda@disroot.org>
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
 *
 */

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*

fun Project.shadowedKotlinProject() {
    val build = tasks.named("build")
    val copyLicense = tasks.named("copyLicense")
    val shadowJar by tasks.existing(ShadowJar::class)
    val jar by tasks.existing(Jar::class)

    shadowJar {
        archiveFileName.set(jar.get().archiveFileName)
    }

    shadowJar.configure {
        mustRunAfter(copyLicense)
    }

    build {
        dependsOn(shadowJar)
    }
}