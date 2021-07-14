import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*

fun Project.shadowedKotlinProject() {
    val build = tasks.named("build")
    val copyLicense = tasks.named("copyLicense")
    val shadowJar by tasks.existing(ShadowJar::class)
    val jar by tasks.existing(Jar::class)

    shadowJar {
        archiveFileName.set(jar.get().archiveFileName)
    }

    shadowJar.configure {
        mustRunAfter(copyLicense)
    }

    build {
        dependsOn(shadowJar)
    }
}