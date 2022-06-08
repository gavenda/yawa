import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream

plugins {
    base
    kotlin("jvm") version Version.KOTLIN
}

val gitHash: String = ByteArrayOutputStream().use { outputStream ->
    project.exec {
        commandLine("git")
        args("rev-parse", "--short", "HEAD")
        standardOutput = outputStream
    }
    outputStream.toString().trim()
}

allprojects {
    group = "work.gavenda.yawa"
    version = "1.3.0-SNAPSHOT-$gitHash"

    repositories {
        maven("https://papermc.io/repo/repository/maven-public")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
        maven("https://repo.dmulloy2.net/nexus/repository/public")
        maven("https://nexus.scarsz.me/content/groups/public")
        maven("https://nexus.vankka.dev/repository/maven-public")
        maven("https://m2.dv8tion.net/releases")
        maven("https://jitpack.io")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/") {
            name = "sonatype-oss-snapshots"
        }
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/") {
            name = "s01-sonatype-oss-snapshots"
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
}
