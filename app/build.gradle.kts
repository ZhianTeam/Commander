import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "org.zhian.commander"
    compileSdk = 37

    defaultConfig {
        applicationId = "org.zhian.commander"
        minSdk = 28
        targetSdk = 37
        versionCode = 1784644720
        versionName = "2.0.0-alpha"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }


    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    // 声明并初始化 BOM 变量
    val composeBom = platform("androidx.compose:compose-bom:2026.07.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Material 3 Expressive
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-expressive")

    // Core Compose dependencies
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.10.0")

    // Core Android & Lifecycle
    implementation("androidx.core:core-ktx:1.20.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.12.0")

    // Icons
    implementation("androidx.compose.material:material-icons-extended")

    // Debug tools
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
