plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

kotlinProject("Yawa")
paperPlugin()

dependencies {
    implementation(kotlin("reflect"))
    // Data source
    implementation(Library.VAULT)
    implementation(Library.HIKARICP)
    implementation(Library.PROTOCOL_LIB)
    implementation(Library.Exposed.CORE)
    implementation(Library.Exposed.DAO)
    implementation(Library.Exposed.JDBC)
    implementation(project(":yawa-api"))
}
//
//tasks.shadowJar {
//    dependencies {
//        relocate("com.zaxxer.hikari", "work.gavenda.hikari")
//        exclude(dependency("org.slf4j:.*"))
//    }
//}