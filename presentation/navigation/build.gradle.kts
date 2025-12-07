plugins {
    id("checkmate.android.library")
    id("checkmate.android.compose")
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.segnities007.navigation"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    // presentation modules
    implementation(project(":presentation:common"))
    implementation(project(":presentation:feature:splash"))
    implementation(project(":presentation:feature:login"))
    implementation(project(":presentation:feature:home"))
    implementation(project(":presentation:feature:items"))
    implementation(project(":presentation:feature:templates"))
    implementation(project(":presentation:feature:dashboard"))
    implementation(project(":presentation:feature:setting"))
    implementation(project(":presentation:ui"))
    implementation(project(":domain:usecase"))
    implementation(project(":domain:model"))

    // app module


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)

    // compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.runtime)

    // koin
    implementation(libs.koin.core)
    implementation(libs.koin.androidx.compose)
}
