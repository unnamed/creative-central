plugins {
    id("creative.dist-conventions")
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(libs.creative.serializer.minecraft)
    implementation(project(":creative-central-api"))
    implementation(project(":creative-central-common"))
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")


    implementation("org.bstats:bstats-velocity:3.0.2") // bstats
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

            // relocate bstats
            relocate("org.bstats", "team.unnamed.creative.central.bukkit.lib.bstats")
        }
    }
}