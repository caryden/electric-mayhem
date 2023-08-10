plugins {
    kotlin("jvm") version "1.9.0"
    `java-library`
    id("maven-publish")
}

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

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            // Define artifactId, groupId, and version if they aren't set globally
            groupId = "com.github.caryden"
            artifactId = "electric-mayhem"
            version = "0.1"
        }
    }
}
