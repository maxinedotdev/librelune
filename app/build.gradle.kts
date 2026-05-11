plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

import java.util.Properties

android {
    namespace = "dev.maxine.librelune"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.maxine.librelune"
        minSdk = 31
        targetSdk = 35
        versionCode = 2
        versionName = "0.4.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val keystorePropertiesFile = rootProject.file("keystore.properties")
    val keystoreProperties = Properties()
    if (keystorePropertiesFile.exists()) {
        keystorePropertiesFile.inputStream().use { keystoreProperties.load(it) }
    }

    fun signingValue(key: String): String? {
        val fromFile = keystoreProperties.getProperty(key)
        if (!fromFile.isNullOrBlank()) return fromFile
        val fromGradle = providers.gradleProperty(key).orNull
        if (!fromGradle.isNullOrBlank()) return fromGradle
        return providers.environmentVariable(key).orNull
    }

    val releaseStoreFile = signingValue("LIBRELUNE_RELEASE_STORE_FILE")
    val releaseStorePassword = signingValue("LIBRELUNE_RELEASE_STORE_PASSWORD")
    val releaseKeyAlias = signingValue("LIBRELUNE_RELEASE_KEY_ALIAS")
    val releaseKeyPassword = signingValue("LIBRELUNE_RELEASE_KEY_PASSWORD")

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
