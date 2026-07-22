plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
}

// Deep clean task — removes all build outputs and configuration cache entries.
// Use this when R.jar or other intermediates are locked: ./gradlew deepClean
tasks.register("deepClean") {
    group = "build"
    description = "Deletes all build outputs and configuration cache to release locked intermediates."
    doLast {
        delete(rootProject.layout.buildDirectory)
        delete(project(":app").layout.buildDirectory)
        delete(rootProject.file(".gradle/configuration-cache"))
        println("[deepClean] Build outputs and configuration cache cleared.")
    }
}
