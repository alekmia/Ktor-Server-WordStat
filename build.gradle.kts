plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
}

group = "ru.ifmo"
version = "0.0.1"

allprojects {
    repositories {
        mavenCentral()
    }
}
