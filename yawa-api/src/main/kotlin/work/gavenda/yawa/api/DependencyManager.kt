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

import org.bukkit.plugin.Plugin
import org.w3c.dom.Element
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLClassLoader
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Dependency manager, downloads all necessary dependencies without requiring shadowing, saves few kb of jars on build.
 */
object DependencyManager {

    private val addUrlMethod = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java).apply {
        isAccessible = true
    }

    private const val TAG_SCOPE = "scope"
    private const val TAG_GROUP = "groupId"
    private const val TAG_VERSION = "version"
    private const val TAG_OPTIONAL = "optional"
    private const val TAG_ARTIFACT = "artifactId"
    private const val TAG_DEPENDENCY = "dependency"
    private const val SCOPE_ONE = "provided"
    private const val SCOPE_TWO = "runtime"

    private val factory = DocumentBuilderFactory.newInstance()

    /**
     * Main Repository and Fallback URLs
     */
    private val repositories = mutableListOf(
        "https://jcenter.bintray.com/",
        "https://repo1.maven.org/maven2/"
    )

    /**
     * Add other repositories to lookup for this dependency manager.
     * @param repos list of repositories to look up.
     */
    fun addRepository(vararg repos: String) {
        repositories.addAll(repos.map { it.fixUrl() })
    }

    /**
     * Attempt to download a dependency from maven central.
     * This will attempt to download its `jar` and `pom` file.
     *
     * @param dependency the dependency to be downloaded
     * @param folder the Folder where the files will be saved
     * @param whenDone operation to be ran when they are downloaded, first file is the `jar`, second is the `pom`
     */
    private fun downloadDependency(dependency: Dependency, folder: File, whenDone: (File, File) -> Unit) {
        val jarFile = File(folder, dependency.jarName)
        val pomFile = File(folder, dependency.pomName)
        val isSnapshot = dependency.version.endsWith("-SNAPSHOT")

        if (jarFile.exists() && !isSnapshot) {
            whenDone(jarFile, pomFile)
            return
        }

        if (!folder.exists()) folder.mkdirs()

        try {
            val pomUrl: String
            val jarUrl: String
            val customRepo = dependency.repository
            if (isSnapshot) {
                val metaFile = File(folder, "meta.xml")

                tryDownload(dependency.metaUrl, metaFile, customRepo)

                val latestSnapShot = readLatestSnapshot(dependency, metaFile)
                val latestFileName = dependency.artifactId + "-" + latestSnapShot
                val latestFile = File(folder, latestFileName)

                if (latestFile.exists()) {
                    whenDone(jarFile, pomFile)
                    return
                } else {
                    if (pomFile.exists()) pomFile.delete()
                    if (jarFile.exists()) jarFile.delete()
                }

                pomUrl = dependency.baseUrl + latestFileName + ".pom"
                jarUrl = dependency.baseUrl + latestFileName + ".jar"

                latestFile.createNewFile()
            } else {
                pomUrl = dependency.pomUrl
                jarUrl = dependency.jarUrl
            }

            tryDownload(pomUrl, pomFile, customRepo)
            tryDownload(jarUrl, jarFile, customRepo)

            whenDone(jarFile, pomFile)
        } catch (e: Exception) {
            e.printStackTrace()
            apiLogger.error("Failed to download dependency $dependency")
        }
    }

    @Throws(Exception::class)
    private fun tryDownload(fileUrl: String, file: File, vararg customUrl: String) {
        apiLogger.debug("Attempting to download $fileUrl")

        if (customUrl.isNotEmpty() && customUrl[0].isNotEmpty()) {
            openStream(customUrl[0] + fileUrl) { url, stream ->
                pullFromStreamToFile(stream, url, file)
            }
            return
        }
        for (url in repositories) {
            val actualUrl = url + fileUrl
            apiLogger.debug("URL: $actualUrl")
            try {
                openStream(actualUrl) { fUrl, stream ->
                    pullFromStreamToFile(
                        stream,
                        fUrl,
                        file
                    )
                }
                return
            } catch (e: IOException) {
                apiLogger.warn("Failed to download from repo: $url")
            }
        }
        apiLogger.error("Failed to download: $fileUrl")
    }

    @Throws(IOException::class)
    private fun openStream(url: String, block: (String, InputStream) -> Unit) {
        URL(url).openStream().use { stream -> block(url, stream) }
    }

    /**
     * This will download the file this stream points to.
     * After downloading this will also validate the file with its SHA-1 hash.
     *
     * @param stream The url stream pointing to the root of the repository
     * @param url The url extension pointing to the file
     * @param file The local file it will be saved to
     */
    private fun pullFromStreamToFile(stream: InputStream, url: String, file: File) {
        try {
            file.outputStream().use { stream.copyTo(it) }

            if (!file.name.endsWith(".jar")) return

            openStream("$url.sha1") { _, shaStream ->
                val mavenSha1 = shaStream.bufferedReader().use(BufferedReader::readText)
                val fileSha1 = file.sha1()
                apiLogger.info("Maven SHA-1: $mavenSha1, File SHA-1: $fileSha1")
                if (mavenSha1.equals(fileSha1, ignoreCase = true).not()) {
                    file.delete()
                    throw IllegalStateException("Failed to validate downloaded file ${file.name}")
                }
                apiLogger.info("File ${file.name} passed validation")
            }

        } catch (e: IOException) {
            e.printStackTrace()
            apiLogger.error("Failed to download url to file ${file.name}")
        }
    }

    /**
     * Read all necessary dependencies from a pom file and return them as [Dependency] instances
     *
     * @param pomFile the pom file
     * @return list of dependencies, empty if none
     */
    private fun readDependencies(pomFile: File): List<Dependency> {
        val dependencies = mutableListOf<Dependency>()
        try {
            val document = readDocument(pomFile)
            val pomDependencies = document.getElementsByTagName(TAG_DEPENDENCY) ?: return emptyList()
            for (i in 0 until pomDependencies.length) {
                val dependency = pomDependencies.item(i) as Element
                val groupId = readTag(dependency, TAG_GROUP)
                val artifactId = readTag(dependency, TAG_ARTIFACT)
                val scope = readTag(dependency, TAG_SCOPE)
                if (scope != SCOPE_ONE && scope != SCOPE_TWO) {
                    apiLogger.info("Skipping $groupId:$artifactId, its scope is '$scope'")
                    continue
                }
                var version = readTag(dependency, TAG_VERSION)
                if (version.startsWith("\${")) {
                    val propertyName = version.substring(2, version.length - 1)
                    version = readTag(document, propertyName)
                }
                val optional = readTag(dependency, TAG_OPTIONAL)
                if (optional.isNotEmpty() && optional.equals("true", ignoreCase = true)) continue
                apiLogger.info("groupId: $groupId, artifactId: $artifactId, version: $version")
                dependencies.add(Dependency(groupId, artifactId, version))
            }
        } catch (e: Exception) {
            apiLogger.error("Failed to load dependencies for pom ${pomFile.name}")
            e.printStackTrace()
            return emptyList()
        }
        return dependencies
    }

    /**
     * Reads the latest snapshot version from a meta file, and then deletes it
     *
     * @param dependency the dependency
     * @param metaFile the meta file
     *
     * @return The version with "SNAPSHOT" replaced with the latest
     */
    private fun readLatestSnapshot(dependency: Dependency, metaFile: File): String {
        try {
            val document: Element = readDocument(metaFile)
            val snapshot: Element = document.getElementsByTagName("snapshot").item(0) as Element
            val timestamp = readTag(snapshot, "timestamp")
            val buildNumber = readTag(snapshot, "buildNumber")
            val latestSnapshot = dependency.version.replace("SNAPSHOT", "$timestamp-$buildNumber")
            apiLogger.info("Latest snapshot version of $dependency is $latestSnapshot")

            metaFile.delete()

            return latestSnapshot
        } catch (e: Exception) {
            apiLogger.error("Failed to load meta for snapshot of  $dependency")
            e.printStackTrace()
        }
        return "ERROR"
    }

    private fun readDocument(file: File): Element {
        return factory.newDocumentBuilder()
            .parse(file)
            .apply { normalize() }
            .run { documentElement }
    }

    private fun readTag(element: Element, tagName: String): String {
        val item = element.getElementsByTagName(tagName).item(0) ?: return ""
        return item.textContent
    }

    /**
     * Loads dependencies into the plugin's classpath.
     * @param plugin the plugin
     * @param dependencies dependencies to load
     */
    fun loadDependencies(plugin: Plugin, dependencies: List<Dependency>) {
        // Download files
        dependencies.forEach {
            loadDependency(plugin, it)
        }
    }

    /**
     * Loads a dependency into the plugin's classpath.
     * @param plugin the plugin
     * @param dependency the dependency to load
     */
    fun loadDependency(plugin: Plugin, dependency: Dependency) {
        val data = File(plugin.dataFolder, "lib").apply { mkdirs() }

        downloadDependency(dependency, data) { jarFile, pomFile ->
            if (!jarFile.exists() || !pomFile.exists()) {
                apiLogger.info("pom file downloaded -> " + jarFile.exists())
                apiLogger.info("jar file downloaded -> " + pomFile.exists())
            } else {
                loadJar(plugin, jarFile)
                loadChildren(plugin, dependency, pomFile)
            }
        }
    }

    /**
     * Loads the jar file into the plugin's class path.
     * @param plugin the plugin
     * @param file the jar file
     */
    @Throws(RuntimeException::class)
    private fun loadJar(plugin: Plugin, file: File) {
        // get the classloader to load into
        val classLoader: ClassLoader = plugin.javaClass.classLoader
        if (classLoader is URLClassLoader) {
            addUrlMethod.invoke(classLoader, file.toURI().toURL())
        } else {
            throw RuntimeException("Unknown classloader: ${classLoader.javaClass}")
        }
    }

    /**
     * Loads the transitive children of the dependency.
     * @param plugin the plugin
     * @param dependency the dependency to load children
     * @param pomFile the pom file
     */
    private fun loadChildren(plugin: Plugin, dependency: Dependency, pomFile: File) {
        apiLogger.info("Loading child dependencies of $dependency")
        val children = readDependencies(pomFile)

        if (children.isEmpty()) return

        children.forEach { child ->
            child.parent = dependency
            loadDependency(plugin, child)
        }
    }

}