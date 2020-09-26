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

package work.gavenda.yawa.api

/**
 * Represents a maven dependency.
 */
data class Dependency(
    val groupId: String,
    val artifactId: String,
    val version: String,
    val repository: String = "",
    var parent: Dependency? = null
) {

    /**
     * The Base URL of this [Dependency].
     * This URL denotes the path to all pertinent resources of this [Dependency].
     */
    val baseUrl = groupId.replace('.', '/') + '/' + artifactId + '/' + version + '/'

    /**
     * Get the URL Pointing to this Dependency's Jar file in Central.
     * @return The URL pointing to its Jar
     */
    val jarUrl = baseUrl + jarName

    /**
     * Get the URL Pointing to this Dependency's POM file in Central.
     * @return The URL pointing to its POM
     */
    val pomUrl = baseUrl + pomName

    /**
     * Gets the URL pointing to this Dependency's snapshot metadata file in the repo.
     * @return The URL pointing to its Snapshot metadata
     */
    val metaUrl = baseUrl + "maven-metadata.xml"

    override fun toString(): String = "$groupId:$artifactId:$version"

    /**
     * The name of this Dependency's Jar file in the Repo
     * @return The jar name
     */
    val jarName get() = "$artifactId-$version.jar"

    /**
     * The name of this Dependency's POM file in the Repo
     * @return The pom name
     */
    val pomName get() = "$artifactId-$version.pom"
}
