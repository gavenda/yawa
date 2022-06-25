plugins {
    `kotlin-dsl`
}

dependencies {
    // Hacky way to add versionCatalogs to "main"
    implementation(libs.kotlin.plugin)
    implementation(libs.kotlin.plugin.serialization)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(files(mcLibs.javaClass.superclass.protectionDomain.codeSource.location))
}