plugins {
    id("checkmate.android.library")
}

android {
    namespace = "com.segnities007.usecase"
}

dependencies {
    implementation(project(":domain:model"))
    implementation(project(":domain:repository"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)
}