plugins {
    id("java")
}

fun ifProp(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)?.let(consumer)
}

fun prop(name: String): String {
    return findProperty(name)?.toString()
        ?: throw IllegalArgumentException("Missing property: $name")
}

group = "com.danrus"
version = "1.0.1"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("io.netty:netty-buffer:${prop("deps.netty")}")
}