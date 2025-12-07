plugins {
    id("checkmate.android.library")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.segnities007.model"
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
}
