plugins {
    id("java")
    id("maven-publish")
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

group = "com.danrus.csc"
version = "1.0.1"

base {
    archivesName.set("csc-paper-"+prop("deps.mc"))
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

val shadowImplementation: Configuration by configurations.creating

dependencies {
    compileOnly("org.jetbrains:annotations:26.0.2")
    paperweight.paperDevBundle("${prop("deps.mc")}-R0.1-SNAPSHOT")

    val commonProject = sc.node.sibling("common")?.project?.path ?: error("Sibling project 'common' not found")
    compileOnly(project(commonProject))
    shadowImplementation(project(commonProject))
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    withSourcesJar()
    withJavadocJar()
}

configurations {
    compileClasspath.get().extendsFrom(shadowImplementation)
    runtimeClasspath.get().extendsFrom(shadowImplementation)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "csc-paper-${prop("deps.mc")}"
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/${System.getenv("GITHUB_REPOSITORY") ?: "Danrus1100/ClientServerCommunicationApi"}")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: ""
                password = System.getenv("GITHUB_TOKEN") ?: ""
            }
        }
    }
}

tasks {
    runServer {
        minecraftVersion(prop("deps.mc"))
    }
    shadowJar {
        archiveClassifier.set("shaded")
        configurations = listOf(shadowImplementation)
        mergeServiceFiles()
    }
    assemble {
        dependsOn(shadowJar)
    }
    processResources {
        inputs.property("minecraft_version", prop("deps.mc"))
        filesMatching("plugin.yml") {
            expand("minecraft_version" to prop("deps.mc"))
        }
    }
}