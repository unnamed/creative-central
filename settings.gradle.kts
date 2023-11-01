plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
}

rootProject.name = "creative-central-parent"

includePrefixed("api")
includePrefixed("common")
includePrefixed("bukkit")
//includePrefixed("minestom")

fun includePrefixed(name: String) {
    val kebabName = name.replace(':', '-')
    val path = name.replace(':', '/')

    include("creative-central-$kebabName")
    project(":creative-central-$kebabName").projectDir = file(path)
}