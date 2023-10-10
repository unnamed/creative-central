plugins {
    id("creative.dist-conventions")
}

repositories {
    maven("https://jitpack.io") // for minestom
}

dependencies {
    implementation(libs.creative.serializer.minecraft)
    implementation(project(":creative-central-api"))
    implementation(project(":creative-central-common"))
    compileOnly("com.github.Minestom:Minestom:2cdb3911b0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    shadowJar {
        dependencies {
            // all these dependencies are provided by the server
            exclude(dependency("com.google.code.gson:gson"))
            exclude(dependency("net.kyori:adventure-api"))
            exclude(dependency("net.kyori:adventure-key"))
            exclude(dependency("net.kyori:examination-api"))
            exclude(dependency("net.kyori:examination-string"))
        }
    }
}