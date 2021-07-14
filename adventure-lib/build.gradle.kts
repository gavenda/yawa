plugins {
    kotlin("jvm")
}

kotlinProject("AdventureLib")

dependencies {
    compileOnly(Library.PAPER)
}