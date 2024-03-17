import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
//    kotlin("plugin.serialization") version "1.9.0"
}

android {
    namespace = "edu.ncssm.ftc.electricmayhem"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    compileOnly("org.firstinspires.ftc:RobotCore:9.1.0")
    compileOnly("org.firstinspires.ftc:Hardware:9.1.0")
    compileOnly("org.firstinspires.ftc:RobotServer:9.1.0")
    compileOnly("org.firstinspires.ftc:FtcCommon:9.1.0")
    compileOnly("org.firstinspires.ftc:Vision:9.1.0")
    implementation("org.nanohttpd:nanohttpd-websocket:2.3.1") {
        exclude(group="fi.iki.elonen", module="nanohttpd")
    }
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // these are for the kotest tests
//    testImplementation("ch.qos.logback:logback-classic:1.4.11")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:1.9.23")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.8.1")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.8.1")
    testImplementation("io.kotest:kotest-property-jvm:5.8.1")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["release"])

                groupId = "edu.ncssm"
                artifactId = "electric-mayhem"
                version = "0.2"
            }
        }
    }
}
