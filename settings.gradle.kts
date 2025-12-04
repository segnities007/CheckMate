pluginManagement {
    includeBuild("build-logic")
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
include(":domain:usecase")
include(":core:navigation")
include(":core:common")
include(":presentation:designsystem")
include(":presentation:ui")
include(":presentation:feature:login")
include(":presentation:feature:splash")
include(":presentation:feature:home")
include(":presentation:feature:setting")
include(":presentation:feature:items")
include(":presentation:feature:dashboard")
include(":presentation:feature:templates")
include(":widget")