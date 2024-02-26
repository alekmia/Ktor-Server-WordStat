plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinx.serialization)

    alias(libs.plugins.ktlint)
}

application {
    mainClass.set("ru.ifmo.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

ktlint {
    version = libs.versions.ktlint.tool.get()
}

tasks.test.configure {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.serialization.json)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.contentNegotiation)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.ktor.server.test.host)
}
