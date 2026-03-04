import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "app.xswallet"

    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "app.xswallet"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.1-Alpha"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val currentBuildTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        buildConfigField("String", "BUILD_TIME", "\"$currentBuildTime\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.03.00"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.03.00"))

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3-window-size-class:1.2.1")

    implementation("androidx.compose.material3:material3:1.5.0-alpha15") {
        version {
            strictly("1.5.0-alpha15")
        }
    }

    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.security:security-crypto:1.0.0")
    implementation("com.mikepenz:multiplatform-markdown-renderer-m3:0.27.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}