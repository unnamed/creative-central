plugins {
    id("creative.java-conventions")
    id("com.github.johnrengelman.shadow")
}

tasks {
    processResources {
        filesMatching(listOf("**plugin.yml", "**extension.json")) {
            expand("project" to project)
        }
    }
}