pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.google.com") }
    }
}

rootProject.name = "CheckMate"
include(":app")
include(":data:repository")
include(":data:local")
include(":data:remote")
include(":domain:model")
include(":domain:repository")
include(":core:navigation")
include(":core:common")
include(":core:ui")
include(":presentation:login")
include(":presentation:auth")
include(":presentation:splash")
include(":presentation:hub")
include(":presentation:home")
include(":presentation:setting")
include(":presentation:items")
include(":presentation:dashboard")
include(":presentation:templates")
