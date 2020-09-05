/**
 * We needed this as a its own classpath provider plugin due to the way it works.
 * Simply relocating the package name does not work unless I modify the META-INF/services as well
 * in which I would not dive deeper.
 */

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

version = Version.EXPOSED

shadowedKotlinProject("Exposed")
paperPlugin()

dependencies {
    implementation(kotlin("reflect"))
    implementation(Library.Exposed.CORE)
    implementation(Library.Exposed.DAO)
    implementation(Library.Exposed.JDBC)
}

// Also manual process of resources instead of using utilities from buildSrc
tasks.processResources {
    filesMatching("plugin.yml") {
        expand(project.properties)
    }
}

tasks.shadowJar {
    dependencies {
        exclude(dependency("org.slf4j:.*"))
        exclude(dependency("org.jetbrains.kotlin:kotlin-reflect:.*"))
    }
}
