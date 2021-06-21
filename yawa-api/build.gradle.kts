plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

kotlinProject("YawaAPI")
spigotPlugin()

dependencies {
    compileOnly(Library.PROTOCOL_LIB)
}
