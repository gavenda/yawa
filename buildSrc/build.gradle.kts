plugins {
    `kotlin-dsl`
}

dependencies {
    // Hacky way to add versionCatalogs to "main"
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(files(mcLibs.javaClass.superclass.protectionDomain.codeSource.location))
}