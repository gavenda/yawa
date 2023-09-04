import org.gradle.jvm.tasks.Jar

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
}

group = "work.gavenda.yawa"
version = "1.5.0"

dependencies {
    compileOnly(libs.kotlin.stdlib)
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.hikari)
    compileOnly(libs.bundles.exposed) {
        isTransitive = false
    }

    // Minecraft Libraries
    compileOnly(mcLibs.protocol.lib)
    compileOnly(mcLibs.paper.api)
    compileOnly(mcLibs.discordsrv)
    compileOnly(mcLibs.vault)
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of("17"))
    }
}

tasks.register("version") {
    group = "help"
    description = "Prints project version."

    doLast {
        println(project.version)
    }
}

tasks.register<Copy>("copyLicense") {
    val srcDir = layout.projectDirectory.file("LICENSE")
    val distDir = layout.buildDirectory.file("resources/main/META-INF")

    from(srcDir)
    into(distDir)
}

tasks.named("processResources", ProcessResources::class) {
    filesMatching("plugin.yml") {
        expand(project.properties)
    }
}

tasks.register<Copy>("package") {
    group = "distribution"
    description = "Publish minecraft plugin to local server testing directory"

    val srcDir = layout.buildDirectory.file("libs")
    val distDir = layout.projectDirectory.file("yawa-server/plugins")
    from(srcDir)
    into(distDir)

    dependsOn("build")
}

tasks.named("jar", Jar::class) {
    val sys = System.getProperties()
    val jdk = "${sys["java.version"]} ${sys["java.vendor"]} ${sys["java.vm.version"]}"
    val os = "${sys["os.name"]} ${sys["os.arch"]} ${sys["os.version"]}"
    val user = sys["user.name"]
    val gradleVer = "Gradle ${gradle.gradleVersion}"

    manifest {
        attributes(
            "Built-By" to user,
            "Created-By" to gradleVer,
            "Build-System" to os,
            "Build-Jdk" to jdk,
            "Kotlin-Version" to "",
            "Implementation-Version" to project.version,
            "Implementation-Title" to name,
            "Implementation-Vendor" to "Gavenda",
            "Paper-Version" to ""
        )
    }

    mustRunAfter("copyLicense")
}

tasks.named("build") {
    dependsOn("copyLicense")
}