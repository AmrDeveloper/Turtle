import com.android.build.api.variant.FilterConfiguration

val supportedAbiCodesMap = mapOf(
    "armeabi-v7a" to 1,
    "arm64-v8a" to 2,
    "x86" to 3,
    "x86_64" to 4,
)

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "com.amrdeveloper.turtle"

    defaultConfig {
        applicationId = "com.amrdeveloper.turtle"
        minSdk = libs.versions.minSdk.get().toInt()

        versionCode = 33
        versionName = "2.0.12"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // App Bundle for Google Play
    bundle {
        language { enableSplit =  false }
        density { enableSplit = false }
        abi { enableSplit = false }
    }

    val isBundleTask: Boolean by lazy {
        gradle.startParameter.taskRequests.any { request ->
            request.args.any { taskName ->
                taskName.contains(other = "bundle", ignoreCase = true)
            }
        }
    }

    // APK's for the Github and Open source app stores
    splits {
        abi {
            isEnable = isBundleTask.not()
            reset()
            include(includes = supportedAbiCodesMap.keys.toTypedArray())
            isUniversalApk = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            // Keep PNGs as they are in the source
            isCrunchPngs = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    packaging {
        jniLibs {
            keepDebugSymbols += "**/*.so"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    project.extensions.extraProperties["android.buildConfig.generateBuildTime"] = false

    dependenciesInfo {
        // Disables dependency metadata when building APKs (for IzzyOnDroid/F-Droid)
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles (for Google Play)
        includeInBundle = false
    }
}

androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            val abi = output.filters.find { it.filterType == FilterConfiguration.FilterType.ABI }?.identifier
            val abiCode = supportedAbiCodesMap[abi]
            if (abiCode != null) {
                val versionCode = output.versionCode.orNull ?: 0
                output.versionCode.set(10 * versionCode + abiCode)
            }
        }
    }
}

dependencyLocking {
    lockAllConfigurations()
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.datastore)

    implementation(project(path = ":lilo"))
    implementation(project(path = ":editor"))
    implementation(project(path = ":terminal"))
    implementation(project(path = ":colorschema"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
