plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

kotlinProject("YawaAPI")
paperPlugin()

dependencies {
    implementation(Library.PROTOCOL_LIB)
}
