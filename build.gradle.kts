tasks.register<Copy>("collectAllJars") {
    group = "build"
    description = "Собирает все JAR-файлы из Fabric и Paper в одну папку"

    destinationDir = file("artifacts")

    subprojects {
        val subproject = this
        tasks.configureEach {
            if (name == "remapJar" || name == "shadowJar" || (subproject.name == "paper" && name == "jar")) {
                val task = this
                if (task is Jar) {
                    from(task.archiveFile) {
                        rename { "${subproject.parent?.name ?: "unknown"}-${subproject.name}-$it" }
                    }
                }
            }
        }
    }
}

tasks.named("build") {
    finalizedBy("collectAllJars")
}