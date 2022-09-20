plugins {
    base
}

val gitHash: String = java.io.ByteArrayOutputStream().use { outputStream ->
    project.exec {
        commandLine("git")
        args("rev-parse", "--short", "HEAD")
        standardOutput = outputStream
    }
    outputStream.toString().trim()
}

allprojects {
    group = "work.gavenda.yawa"
    version = "1.3.8-SNAPSHOT-$gitHash"
}

tasks.register("version") {
    group = "help"
    description = "Prints project version."

    doLast {
        println(project.version)
    }
}