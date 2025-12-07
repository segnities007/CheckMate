plugins {
    id("checkmate.android.library")
}

android {
    namespace = "com.segnities007.repository"
}

dependencies {
    api(project(":domain:model"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)
}
