plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

kotlinProject("YawaAPI")
spigotPlugin()

dependencies {
    implementation(Library.PROTOCOL_LIB)
}
