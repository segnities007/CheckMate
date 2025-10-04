plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.segnities007.common"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":domain:repository"))
    implementation(project(":domain:model"))
    implementation(project(":domain:usecase"))
    implementation(project(":data:remote"))
    implementation(project(":data:repository"))
    implementation(project(":data:local"))
    implementation(project(":presentation:login"))
    implementation(project(":presentation:splash"))
    implementation(project(":presentation:auth"))
    implementation(project(":presentation:hub"))
    implementation(project(":presentation:dashboard"))
    implementation(project(":presentation:home"))
    implementation(project(":presentation:items"))
    implementation(project(":presentation:setting"))
    implementation(project(":presentation:templates"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // koin
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
}
