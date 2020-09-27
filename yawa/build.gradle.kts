plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

kotlinProject("Yawa")
paperPlugin()

dependencies {
    implementation(kotlin("reflect"))
    // Data source
    implementation(Library.HIKARICP)
    implementation(Library.PROTOCOL_LIB)
    implementation(Library.Exposed.CORE)
    implementation(Library.Exposed.DAO)
    implementation(Library.Exposed.JDBC)
    implementation(project(":yawa-api"))
}