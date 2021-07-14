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
    compileOnly(Library.LOG4J2)
    compileOnly(Library.PROTOCOL_LIB)
}