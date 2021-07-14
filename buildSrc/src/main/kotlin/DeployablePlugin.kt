import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.*

/**
 * Configures the current project with the `deployPlugin` task.
 */
fun Project.deployablePlugin() {
    val build = tasks.named("build")

    tasks.register<Copy>("deployPlugin") {
        dependsOn(build)

        val srcDir = file("$buildDir/libs")
        val distDir = file("$rootDir/yawa-server/plugins")

        from(srcDir)
        into(distDir)
    }
}