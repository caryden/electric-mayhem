plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "edu.ncssm.ftc.electricmayhem"
    compileSdk = 34

    defaultConfig {
        applicationId = "edu.ncssm.ftc.electricmayhem"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation(project(mapOf("path" to ":electric-mayhem")))
    compileOnly("org.firstinspires.ftc:RobotCore:9.0.1")
    compileOnly("org.firstinspires.ftc:Vision:9.0.1")
    compileOnly("org.firstinspires.ftc:Hardware:9.0.1")

    // these are for the kotest tests
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.kotest:kotest-property:5.6.2")

}