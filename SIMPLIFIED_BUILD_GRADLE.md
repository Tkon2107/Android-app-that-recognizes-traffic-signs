# Simplified Build Configuration (Without Room Database)

If you're having issues with Kotlin/kapt, here's a simplified version that uses only JSON storage without Room database.

## Option 1: Try This First

Sync Gradle with the current build.gradle. The kapt plugin should work now.

```
File → Sync Project with Gradle Files
```

## Option 2: Simplified Version (If Option 1 Fails)

If you still get errors, replace `app/build.gradle` with this simplified version:

```gradle
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace 'com.trafficsignsclassification'
    compileSdk 34

    defaultConfig {
        applicationId "com.trafficsignsclassification"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0 (Beta)"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField "boolean", "ENABLE_EVALUATION", "true"
            buildConfigField "boolean", "ENABLE_PERFORMANCE_OVERLAY", "true"
        }
        release {
            minifyEnabled false
            buildConfigField "boolean", "ENABLE_EVALUATION", "false"
            buildConfigField "boolean", "ENABLE_PERFORMANCE_OVERLAY", "false"
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }
    
    buildFeatures {
        viewBinding true
        buildConfig true
    }
}

dependencies {
    // TensorFlow Lite
    implementation 'org.tensorflow:tensorflow-lite:2.16.1'
    implementation 'org.tensorflow:tensorflow-lite-support:0.4.4'

    // OpenCV
    implementation project(":opencv")

    // CameraX
    def camerax_version = "1.3.1"
    implementation "androidx.camera:camera-core:${camerax_version}"
    implementation "androidx.camera:camera-camera2:${camerax_version}"
    implementation "androidx.camera:camera-lifecycle:${camerax_version}"
    implementation "androidx.camera:camera-view:${camerax_version}"

    // Kotlin Coroutines (for async operations)
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3'

    // Gson (for JSON parsing)
    implementation 'com.google.code.gson:gson:2.10.1'

    // Material Design
    implementation 'com.google.android.material:material:1.12.0'

    implementation libs.appcompat
    implementation libs.material
    implementation libs.activity
    implementation libs.constraintlayout
    testImplementation libs.junit
    androidTestImplementation libs.ext.junit
    androidTestImplementation libs.espresso.core
}
```

## Changes in Simplified Version

**Removed:**
- Kotlin plugin
- kapt plugin
- Room database dependencies
- Kotlin stdlib

**Kept:**
- All existing dependencies
- Gson for JSON parsing
- Coroutines for async operations
- BuildConfig flags

## What This Means

With the simplified version:

✅ **Still Works:**
- Penalty lookup (using JSON only, no database)
- Performance monitoring
- Offline evaluation
- All UI features

❌ **Limitations:**
- Slower penalty queries (reads JSON each time)
- No caching in database
- Kotlin files need to be converted to Java

## If Using Simplified Version

You'll need to convert the Kotlin files to Java or use a simpler JSON-based approach. I can provide Java versions of all the penalty module files if needed.

## Recommendation

**Try Option 1 first** - the kapt plugin should work now. Only use Option 2 if you continue to have issues.
