plugins {
    kotlin("jvm") version "1.9.0"
    `java-library`
}

group = "edu.ncssm"
version = "0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2") // Use the latest version here
//    testImplementation(kotlin("test"))

    testImplementation("io.kotest:kotest-runner-junit5:5.6.2") // replace 5.x.x with the latest version
    testImplementation("io.kotest:kotest-assertions-core:5.6.2")
    testImplementation("io.kotest:kotest-property:5.6.2")
}

tasks.test {
    useJUnitPlatform()
}