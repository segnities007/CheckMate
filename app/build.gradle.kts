import java.io.FileInputStream
import java.util.Properties

plugins {
    id("checkmate.android.application")
    id("checkmate.android.compose")
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.segnities007.checkmate"

    defaultConfig {
        applicationId = "com.segnities007.checkmate"
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // local.propertiesからAPIキーを読み込む
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }

        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"${localProperties.getProperty("GEMINI_API_KEY", "")}\""
        )
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

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/INDEX.LIST"
        }
    }
}

dependencies {
    implementation(project(":core:navigation"))
    implementation(project(":core:common"))
    implementation(project(":domain:repository"))
    implementation(project(":domain:model"))
    implementation(project(":domain:usecase"))
    implementation(project(":data:remote"))
    implementation(project(":data:repository"))
    implementation(project(":data:local"))
    implementation(project(":presentation:ui"))
    implementation(project(":presentation:feature:login"))
    implementation(project(":presentation:feature:splash"))
    implementation(project(":presentation:feature:dashboard"))
    implementation(project(":presentation:feature:home"))
    implementation(project(":presentation:feature:items"))
    implementation(project(":presentation:feature:setting"))
    implementation(project(":presentation:feature:templates"))
    implementation(project(":widget"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // koin
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    // serialization
    implementation(libs.kotlinx.serialization.json)

    // navigation
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // kotlinx-datetime
    implementation(libs.kotlinx.datetime)

    // room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
}
