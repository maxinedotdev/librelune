plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "dev.maxine.librelune"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.maxine.librelune"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val releaseStoreFile = providers.gradleProperty("LIBRELUNE_RELEASE_STORE_FILE")
        .orElse(providers.environmentVariable("LIBRELUNE_RELEASE_STORE_FILE"))
        .orNull
    val releaseStorePassword = providers.gradleProperty("LIBRELUNE_RELEASE_STORE_PASSWORD")
        .orElse(providers.environmentVariable("LIBRELUNE_RELEASE_STORE_PASSWORD"))
        .orNull
    val releaseKeyAlias = providers.gradleProperty("LIBRELUNE_RELEASE_KEY_ALIAS")
        .orElse(providers.environmentVariable("LIBRELUNE_RELEASE_KEY_ALIAS"))
        .orNull
    val releaseKeyPassword = providers.gradleProperty("LIBRELUNE_RELEASE_KEY_PASSWORD")
        .orElse(providers.environmentVariable("LIBRELUNE_RELEASE_KEY_PASSWORD"))
        .orNull

    val hasReleaseSigning = !releaseStoreFile.isNullOrBlank() &&
        !releaseStorePassword.isNullOrBlank() &&
        !releaseKeyAlias.isNullOrBlank() &&
        !releaseKeyPassword.isNullOrBlank()

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(releaseStoreFile!!)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.activity)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)

    implementation(libs.glance.appwidget)
    implementation(libs.glance.material3)

    implementation(libs.datastore.preferences)
    implementation(libs.work.runtime.ktx)
    implementation(libs.startup.runtime)
    implementation(libs.commons.suncalc)

    testImplementation(kotlin("test"))
}
