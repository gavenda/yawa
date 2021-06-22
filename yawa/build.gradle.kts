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
    compileOnly(Library.KYORI)
    compileOnly(Library.KYORI_BUKKIT)
    compileOnly(Library.KYORI_MINIMESSAGE)

    // Exposed
    implementation(Library.Exposed.CORE)
    implementation(Library.Exposed.DAO)
    implementation(Library.Exposed.JDBC)
}