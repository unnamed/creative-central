plugins {
    id("creative.java-conventions")
}

dependencies {
    implementation(project(":creative-central-api"))
    implementation("team.unnamed:creative-serializer-minecraft:$version")
    implementation("team.unnamed:creative-server:0.7.2-SNAPSHOT")
}