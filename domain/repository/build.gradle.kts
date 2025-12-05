plugins {
    id("checkmate.jvm.library")
}

dependencies {
    implementation(project(":domain:model"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)
}
