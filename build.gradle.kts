import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    base
    kotlin("jvm") version Version.KOTLIN
}

allprojects {
    group = "work.gavenda.yawa"
    version = "1.0.0"

    repositories {
        jcenter()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
        maven("https://papermc.io/repo/repository/maven-public")
        maven("https://repo.dmulloy2.net/nexus/repository/public")
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

    tasks.register<Copy>("deployPlugin") {
        dependsOn("build")

        val srcDir = file("$rootDir/build/libs")
        val distDir = file("$rootDir/yawa-server/plugins")

        from(srcDir)
        into(distDir)
    }

    afterEvaluate {
        tasks.build {
            finalizedBy("copyArtifacts")
        }
    }
}
