import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    java
    id("org.jetbrains.kotlin.jvm")
}

extra["PUBLISH_NAME"] = "Amplitude Kotlin Core"
extra["PUBLISH_DESCRIPTION"] = "Amplitude Kotlin Core library"
extra["PUBLISH_ARTIFACT_ID"] = "analytics-core"

apply(from = "${rootDir}/gradle/publish-module.gradle.kts")

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.kotlin.stdlib)

    compileOnly(libs.json)
    compileOnly(libs.okhttp)
    implementation(libs.coroutines.core)

    testImplementation(libs.json)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockwebserver)
}

tasks.named<DokkaTaskPartial>("dokkaHtmlPartial") {
    failOnWarning.set(true)
}
