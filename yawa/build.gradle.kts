import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val shadowJar by tasks.existing(ShadowJar::class)

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

kotlinProject("Yawa")
spigotPlugin()

dependencies {
    // Runtime JAR files in the classpath should have the same version. These files were found in the classpath
    // Yes it is to fix the above
    compileOnly(kotlin("reflect"))

    // Implement our own api
    compileOnly(project(":yawa-api"))

    // Data source
    implementation(Library.HIKARICP)

    // Minecraft plugins
    compileOnly(Library.VAULT)
    compileOnly(Library.DISCORDSRV)
    compileOnly(Library.PROTOCOL_LIB)

    // Exposed
    implementation(Library.Exposed.CORE)
    implementation(Library.Exposed.DAO)
    implementation(Library.Exposed.JDBC)
}

shadowJar {
    relocate("com.zaxxer.hikari", "work.gavenda.yawa.lib.hikari")
    relocate("net.kyori", "work.gavenda.yawa.lib.kyori")
}