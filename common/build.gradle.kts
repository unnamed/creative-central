plugins {
    id("creative.java-conventions")
}

dependencies {
    implementation(project(":creative-central-api"))
    implementation(libs.creative.serializer.minecraft)
    implementation(libs.creative.server)

    // -- provided by server or plugin implementations --
    compileOnly(libs.adventure.text.minimessage)
    compileOnly("org.yaml:snakeyaml:2.0")
}