plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

kotlinProject("YawaAPI")
spigotPlugin()

dependencies {
    implementation(Library.KYORI)
    implementation(Library.KYORI_BUKKIT)
    implementation(Library.KYORI_MINIMESSAGE)
    compileOnly(Library.PROTOCOL_LIB)
}