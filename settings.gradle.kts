pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven ("https://repo.papermc.io/repository/maven-public/")
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.8.3"
}

stonecutter {
    // Configuration goes here
    create(rootProject) {
        versions(/*"1.21.4", "1.21.5", */ "1.21.8" /*, "1.21.10" */, "1.21.11")
        // version("26.1-snapshot", "26.1").buildscript("unobfuscated.gradle.kts")
        branch("common")
        branch("fabric")
        branch("paper")
    }
}

//include("common")

rootProject.name = "ClientServerCommunicationApi"