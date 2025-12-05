import java.io.FileInputStream
import java.util.Properties

plugins {
    id("checkmate.android.library")
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.segnities007.repository"
    
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        // local.propertiesからAPIキーを読み込む
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }
        buildConfigField("String", "GEMINI_API_KEY", "\"${localProperties.getProperty("GEMINI_API_KEY", "")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":domain:repository"))
    implementation(project(":data:remote"))
    implementation(project(":data:local"))
    implementation(project(":domain:model"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // datetime
    implementation(libs.kotlinx.datetime)

    // serialization
    implementation(libs.kotlinx.serialization.json)

    // datetime
    implementation(libs.kotlinx.datetime)

    // Gemini AI
    implementation(libs.google.ai.client)

    // datetime (GeminiAiService用)
    implementation(libs.kotlinx.datetime)
}
