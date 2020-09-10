plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

shadowedKotlinProject("Yawa")
paperPlugin()

dependencies {
    implementation(kotlin("reflect"))
    // Data source
    implementation(Library.HIKARICP)

    compileOnly(Library.PROTOCOL_LIB)
    compileOnly(Library.Exposed.CORE)
    compileOnly(Library.Exposed.DAO)
    compileOnly(Library.Exposed.JDBC)

    compileOnly(project(":yawa-api"))
}

tasks.shadowJar {
    dependencies {
        relocate("com.zaxxer.hikari", "work.gavenda.hikari")
        exclude(dependency("org.slf4j:.*"))
    }
}