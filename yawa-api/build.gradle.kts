plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

kotlinProject("YawaAPI")
shadowedKotlinProject()
paperPlugin()
deployablePlugin()

dependencies {
    implementation(Library.KYORI_MINIMESSAGE)
    compileOnly("org.apache.logging.log4j:log4j-api:2.14.1")
    compileOnly(Library.PROTOCOL_LIB)
}