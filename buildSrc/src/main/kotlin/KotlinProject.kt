import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*

/**
 * Configures the project as a shadowed Kotlin project.
 */
fun Project.shadowedKotlinProject(archiveName: String) {
    val shadowJar by tasks.existing(ShadowJar::class)
    val jar by tasks.existing(Jar::class)
    val build = tasks.named("build")

    val sysProp = System.getProperties().toMap()

    val javaVersion = "${sysProp["java.version"]} ${sysProp["java.vendor"]} ${sysProp["java.vm.version"]}"
    val operatingSystem = "${sysProp["os.name"]} ${sysProp["os.arch"]} ${sysProp["os.version"]}"

    jar {
        archiveBaseName.set(archiveName)

        manifest {
            attributes(
                "Built-By" to sysProp["user.name"],
                "Created-By" to "Gradle ${gradle.gradleVersion}",
                "Build-OS" to operatingSystem,
                "Build-JDK" to javaVersion,
                "Kotlin-Version" to Version.KOTLIN,
                "Version" to project.version
            )
        }
    }

    dependencies {
        "implementation"(kotlin("stdlib-jdk8"))
    }

    shadowJar {
        // We don't want a suffix
        archiveClassifier.set("")
        archiveBaseName.set(archiveName)
    }

    build {
        dependsOn(shadowJar)
    }
}