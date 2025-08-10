plugins {
    id("creative.dist-conventions")
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://mvn.lumine.io/repository/maven-public/") // Model Engine
    maven("https://jitpack.io/") // ItemsAdder
    maven("https://repo.oraxen.com/releases") // oraxen
}

dependencies {
    implementation(libs.creative.serializer.minecraft)
    implementation(project(":creative-central-api"))
    implementation(project(":creative-central-common"))
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

    compileOnly("com.ticxo.modelengine:ModelEngine:R4.0.3") // Model Engine
    compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.1") // ItemsAdder
    compileOnly("io.th0rgal:oraxen:1.178.0") // oraxen

    implementation("org.bstats:bstats-bukkit:3.0.2") // bstats
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    compileJava {
        options.release = 17
    }
    runServer {
        minecraftVersion("1.20.6")
    }
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