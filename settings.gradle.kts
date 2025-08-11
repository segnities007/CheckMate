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
