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

object Version {
    const val KOTLIN = "1.4.10"
    const val KOTLINX_COROUTINES = "1.3.9"
    const val SPIGOT = "1.16.3-R0.1-SNAPSHOT"
    const val PAPER = "1.16.3-R0.1-SNAPSHOT"
    const val MOCKBUKKIT = "0.5.0"
    const val HIKARICP = "3.4.5"
    const val KODEIN = "7.0.0"
    const val PROTOCOL_LIB = "4.5.1"
    const val EXPOSED = "0.27.1"
    const val JUNIT_JUPITER = "5.6.2"
}

object Library {
    const val KOTLINX_COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.KOTLINX_COROUTINES}"
    const val SPIGOT = "org.spigotmc:spigot-api:${Version.SPIGOT}"
    const val PAPER = "com.destroystokyo.paper:paper-api:${Version.PAPER}"
    const val MOCKBUKKIT = "com.github.seeseemelk:MockBukkit-v1.16:${Version.MOCKBUKKIT}"
    const val HIKARICP = "com.zaxxer:HikariCP:${Version.HIKARICP}"
    const val KODEIN = "org.kodein.di:kodein-di:${Version.KODEIN}"
    const val PROTOCOL_LIB = "com.comphenix.protocol:ProtocolLib:${Version.PROTOCOL_LIB}"

    object JUNIT {
        const val API = "org.junit.jupiter:junit-jupiter-api:${Version.JUNIT_JUPITER}"
        const val ENGINE = "org.junit.jupiter:junit-jupiter-engine:${Version.JUNIT_JUPITER}"
    }

    object Exposed {
        const val CORE = "org.jetbrains.exposed:exposed-core:${Version.EXPOSED}"
        const val DAO = "org.jetbrains.exposed:exposed-dao:${Version.EXPOSED}"
        const val JDBC = "org.jetbrains.exposed:exposed-jdbc:${Version.EXPOSED}"
    }
}