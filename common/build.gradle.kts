plugins {
    id("java")
    id("maven-publish")
}

fun ifProp(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)?.let(consumer)
}

fun prop(name: String): String {
    return findProperty(name)?.toString()
        ?: throw IllegalArgumentException("Missing property: $name")
}

group = "com.danrus.csc"
version = "1.0.1"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("io.netty:netty-buffer:${prop("deps.netty")}")
}

java {
    withSourcesJar()
    withJavadocJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "csc-common-${project.name}"
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
