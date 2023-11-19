plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("gradle.plugin.org.cadixdev.gradle:licenser:0.6.1")
    implementation("com.github.johnrengelman:shadow:8.1.1")
}