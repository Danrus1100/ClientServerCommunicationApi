plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
    id("com.gradleup.shadow") version "9.3.1"
}

fun ifProp(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)
        ?.let(consumer)
}

fun prop(name: String) : String {
    return findProperty(name)?.toString()
        ?: throw IllegalArgumentException("Missing property: $name")
}

group = "com.danrus"
version = "1.0"

base {
    archivesName.set("csc-paper-"+prop("deps.mc"))
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("org.jetbrains:annotations:26.0.2")
    paperweight.paperDevBundle("${prop("deps.mc")}-R0.1-SNAPSHOT")
    implementation(project(sc.node.sibling("common")?.project?.path ?: error("Sibling project 'common' not found")))
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks {
    runServer {
        minecraftVersion(prop("deps.mc"))
    }
    shadowJar {
        archiveClassifier.set("shaded")
        mergeServiceFiles()
    }
    build {
        dependsOn(shadowJar)
    }
    processResources {
        inputs.property("minecraft_version", prop("deps.mc"))
        filesMatching("plugin.yml") {
            expand("minecraft_version" to prop("deps.mc"))
        }
    }
}