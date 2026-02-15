import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("java")
    id("maven-publish")
    id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT"
}

fun ifProp(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)?.let(consumer)
}

fun prop(name: String): String {
    return findProperty(name)?.toString()
        ?: throw IllegalArgumentException("Missing property: $name")
}

version = prop("mod.version")

base {
    archivesName.set(prop("mod.id") + "-fabric-" + prop("deps.mc"))
}

loom {
    runs {
        named("client") {
            property("devauth.enabled", "true")
        }
    }
}

repositories {
    mavenCentral()
    maven("https://maven.parchmentmc.org")
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

dependencies {
    minecraft("com.mojang:minecraft:${prop("deps.mc")}")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${prop("deps.mc")}:${prop("deps.parchment")}@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:${prop("deps.fabric")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${prop("deps.fapi")}")
    modRuntimeOnly("me.djtheredstoner:DevAuth-fabric:1.2.2")

    val commonProjectPath = sc.node.sibling("common")?.project?.path ?: error("Sibling project 'common' not found")

    // Создаем ссылку на проект с конфигурацией namedElements
    implementation(project(commonProjectPath))

    // Для include (включения в jar) используем просто путь к проекту.
    // Loom сам выберет нужный артефакт (remapJar или classes), не создавая цикла.
    include(project(commonProjectPath))
}

tasks {
    processResources {
        inputs.property("version", prop("mod.version"))
        inputs.property("mod_id", prop("mod.id"))
        inputs.property("mc_version", prop("deps.mc"))

        filesMatching("fabric.mod.json") {
            expand("version" to prop("mod.version"), "mod_id" to prop("mod.id"), "mc_version" to prop("deps.mc"))
        }
    }
}

stonecutter.replacements {
    string {
        direction = stonecutter.eval(stonecutter.current.version, ">=1.21.11")
        replace("ResourceLocation", "Identifier")
    }
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = prop("pub.group")
            artifactId = "csc-${project.name}"
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
