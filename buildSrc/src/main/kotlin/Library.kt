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

object Version {
    const val KOTLIN = "1.5.20"
    const val SPIGOT = "1.17.1-R0.1-SNAPSHOT"
    const val PAPER = "1.17.1-R0.1-SNAPSHOT"
    const val PAPER_LIB = "1.0.6"
    const val HIKARICP = "4.0.3"
    const val PROTOCOL_LIB = "4.5.1"
    const val EXPOSED = "0.32.1"
    const val JUNIT_JUPITER = "5.6.2"
    const val VAULT = "1.7"
    const val DISCORDSRV = "1.23.0"
    const val KYORI_MINIMESSAGE = "4.1.0-SNAPSHOT"
    const val SLF4J = "1.7.30"
    const val LOG4J2 = "2.14.1"
}

object Library {
    const val SPIGOT = "org.spigotmc:spigot-api:${Version.SPIGOT}"
    const val PAPER = "io.papermc.paper:paper-api:${Version.PAPER}"
    const val PAPER_LIB = "io.papermc:paperlib:${Version.PAPER_LIB}"
    const val HIKARICP = "com.zaxxer:HikariCP:${Version.HIKARICP}"
    const val PROTOCOL_LIB = "com.comphenix.protocol:ProtocolLib:${Version.PROTOCOL_LIB}"
    const val VAULT = "com.github.MilkBowl:VaultAPI:${Version.VAULT}"
    const val DISCORDSRV = "com.discordsrv:discordsrv:${Version.DISCORDSRV}"
    const val KYORI_MINIMESSAGE = "net.kyori:adventure-text-minimessage:${Version.KYORI_MINIMESSAGE}"
    const val SLF4J = "org.slf4j:slf4j-jdk14:${Version.SLF4J}"
    const val LOG4J2 = "org.apache.logging.log4j:log4j-api:${Version.LOG4J2}"

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