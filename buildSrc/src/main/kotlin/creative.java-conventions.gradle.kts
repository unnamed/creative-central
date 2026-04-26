plugins {
    `java-library`
    id("org.cadixdev.licenser")
}

repositories {
    mavenLocal()
    maven("https://repo.unnamed.team/repository/unnamed-public/")
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.14.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.14.4")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

license {
    header.set(rootProject.resources.text.fromFile("header.txt"))
    include("**/*.java")
    newLine.set(false)
}

tasks {
    test {
        useJUnitPlatform()
    }
}