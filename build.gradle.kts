plugins {
    id("com.android.application") version "8.7.3" apply false
    id("com.android.library") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    alias(libs.plugins.compose.compiler) apply false
    id("androidx.navigation.safeargs") version "2.5.0" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}