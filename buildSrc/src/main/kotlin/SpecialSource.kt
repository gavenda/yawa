import org.gradle.api.Project
import java.io.File

data class MappingPaths(
    val remappedMojang: String,
    val remappedObf: String,
    val mojangMappings: String,
    val spigotMappings: String,
)

val Project.mavenRepository: File
    get() {
        return File(repositories.mavenLocal().url.path)
    }

val Project.specialSource: String
    get() {
        val resolved = configurations.getByName("specialSource")
            .resolvedConfiguration
            .firstLevelModuleDependencies
        val module = resolved.first { it.moduleGroup == "net.md-5" && it.moduleName == "SpecialSource" }
        return module.moduleArtifacts.first().file.path
    }

fun Project.mappingPaths(craftBukkitVersion: String): MappingPaths {
    val remappedMojangPath =
        "org/spigotmc/spigot/${craftBukkitVersion}/spigot-${craftBukkitVersion}-remapped-mojang.jar"
    val remappedObfPath =
        "org/spigotmc/spigot/${craftBukkitVersion}/spigot-${craftBukkitVersion}-remapped-mojang.jar"
    val mojangMappingsPath =
        "org/spigotmc/minecraft-server/${craftBukkitVersion}/minecraft-server-${craftBukkitVersion}-maps-mojang.txt"
    val spigotMappingsPath =
        "org/spigotmc/minecraft-server/${craftBukkitVersion}/minecraft-server-${craftBukkitVersion}-maps-spigot.csrg"

    return MappingPaths(
        remappedMojang = File(mavenRepository, remappedMojangPath).path,
        remappedObf = File(mavenRepository, remappedObfPath).path,
        mojangMappings = File(mavenRepository, mojangMappingsPath).path,
        spigotMappings = File(mavenRepository, spigotMappingsPath).path,
    )
}

fun Project.remapMojangToObfuscated(
    inputFile: File,
    outputFile: File,
    craftBukkitVersion: String
) {
    println("> remapMojangToObfuscated")
    println("  Input: ${inputFile.path}")
    println("  Output: ${outputFile.path}")
    println("  CraftBukkit version: $craftBukkitVersion")
    println("  SpecialSource: $specialSource")

    val map = mappingPaths(craftBukkitVersion)
    val classpathSeparator = System.getProperties()["path.separator"]

    exec {
        commandLine(
            "java", "-cp", "${specialSource}${classpathSeparator}${map.remappedMojang}",
            "net.md_5.specialsource.SpecialSource", "--live",
            "-i", inputFile.path,
            "-o", outputFile.path,
            "-m", map.mojangMappings,
            "--reverse"
        )
        standardOutput = System.out
    }
}


fun Project.remapObfuscatedToSpigot(
    inputFile: File,
    outputFile: File,
    craftBukkitVersion: String
) {
    println("> remapObfuscatedToSpigot")
    println("  Input: ${inputFile.path}")
    println("  Output: ${outputFile.path}")
    println("  CraftBukkit version: $craftBukkitVersion")
    println("  SpecialSource: $specialSource")

    val map = mappingPaths(craftBukkitVersion)
    val classpathSeparator = System.getProperties()["path.separator"]

    exec {
        commandLine(
            "java", "-cp", "${specialSource}${classpathSeparator}${map.remappedObf}", "net.md_5.specialsource.SpecialSource",
            "--live",
            "-i", inputFile.path,
            "-o", outputFile.path, "-m", map.mojangMappings,
            "--reverse"
        )
        standardOutput = System.out
    }
}

fun Project.remapMojangToSpigot(
    inputFile: File,
    intermediateFile: File,
    outputFile: File,
    craftBukkitVersion: String
) {
    println("> remapMojangToSpigot")
    println("  Input: ${inputFile.path}")
    println("  Output: ${outputFile.path}")
    println("  CraftBukkit version: $craftBukkitVersion")

    remapMojangToObfuscated(inputFile, intermediateFile, craftBukkitVersion)
    remapObfuscatedToSpigot(intermediateFile, outputFile, craftBukkitVersion)
}
