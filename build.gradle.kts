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
    version = "1.0.0-SNAPSHOT-$gitHash"

    repositories {
        jcenter()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
        maven("https://papermc.io/repo/repository/maven-public")
        maven("https://repo.dmulloy2.net/nexus/repository/public")
        maven("https://jitpack.io")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

subprojects {
    tasks.register<Copy>("copyArtifacts") {
        val srcDir = file("$buildDir/libs")
        val distDir = file("$rootDir/build/libs")

        from(srcDir)
        into(distDir)
    }

    tasks.register<Copy>("copyLicense") {
        val srcDir = file("$rootDir/LICENSE")
        val distDir = file("$buildDir/resources/main/META-INF/")

        from(srcDir)
        into(distDir)
    }

    tasks.register<Copy>("deployPlugin") {
        dependsOn("build")

        val srcDir = file("$rootDir/build/libs")
        val distDir = file("$rootDir/yawa-server/plugins")

        from(srcDir)
        into(distDir)
    }

    afterEvaluate {
        tasks.build {
            dependsOn("copyLicense")
            finalizedBy("copyArtifacts")
        }
    }
}
