import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*
import org.gradle.language.jvm.tasks.ProcessResources

/**
 * Configures the current project as a Paper plugin.
 */
fun Project.paperPlugin() {
    val processResources by tasks.existing(ProcessResources::class)
    val shadowJar by tasks.existing(ShadowJar::class)
    val jar by tasks.existing(Jar::class)

    dependencies {
        "compileOnly"(Library.PAPER)
        "testImplementation"(Library.PAPER)
    }

    jar {
        manifest {
            attributes(
                "Paper-Version" to Version.PAPER
            )
        }
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(project.properties)
        }
    }

    shadowJar {
        dependencies {
            // Remove Kotlin
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk7:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib-common:.*"))
            exclude(dependency("org.jetbrains:annotations:.*"))
        }
    }

}