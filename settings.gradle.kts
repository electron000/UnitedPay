pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "UnitedPay"

// Application module
include(":app")

// Core modules
include(":core:common")
include(":core:designsystem")
include(":core:security")
include(":core:network")
include(":core:database")
include(":core:model")

// Feature modules
include(":feature:auth")
include(":feature:home")
include(":feature:payment")
include(":feature:passbook")
include(":feature:soundbox")
