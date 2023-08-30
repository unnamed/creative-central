plugins {
    id("creative.java-conventions")
}

dependencies {
    implementation(project(":creative-central-api"))
    implementation("team.unnamed:creative-serializer-minecraft:$version")
    implementation("team.unnamed:creative-server:0.7.2-SNAPSHOT")

    // -- provided by server or plugin implementations --
    compileOnly("net.kyori:adventure-text-minimessage:4.13.1")
    compileOnly("org.yaml:snakeyaml:2.0")
}