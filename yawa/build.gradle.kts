plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

kotlinProject("Yawa")
shadowedKotlinProject()
paperPlugin()
deployablePlugin()

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
    compileOnly(Library.LOG4J2)
    
    // Exposed
    implementation(Library.Exposed.CORE) {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation(Library.Exposed.DAO) {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation(Library.Exposed.JDBC) {
        exclude(group = "org.jetbrains.kotlin")
    }
}

tasks.shadowJar {
    val projectPackage = project.group

    relocate("com.zaxxer.hikari", "$projectPackage.hikari")
}