plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.drawingapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.drawingapp"
        minSdk = 33
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures{
        dataBinding = true
        viewBinding = true
        compose = true
    }

    composeOptions{
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
// Jetpack Compose Dependenciesimplementation 'androidx.compose.ui:ui:1.4.0'
    implementation ("androidx.compose.material:material:1.4.0")
    implementation ("androidx.compose.ui:ui-tooling:1.4.0")
    implementation ("androidx.compose.runtime:runtime-livedata:1.4.0")
    debugImplementation ("androidx.compose.ui:ui-test-junit4:1.4.0")
    debugImplementation ("androidx.navigation:navigation-testing:2.3.5")
    implementation("io.coil-kt:coil-compose:2.4.0")
    debugImplementation("androidx.fragment:fragment-testing:1.4.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation ("androidx.navigation:navigation-testing:2.3.5")

    // For Testing
    androidTestImplementation ("androidx.test:core:1.4.0")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.0")
    androidTestImplementation ("androidx.test.espresso:espresso-idling-resource:3.4.0")
    androidTestImplementation ("androidx.arch.core:core-testing:2.1.0")

    //to get livedata + viewmodel stuff
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")

    //Fragment stuff
    implementation("androidx.fragment:fragment-ktx:1.6.1")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("com.github.antonpopoff:colorwheel:1.1.13")
    implementation("com.github.madrapps:pikolo:2.0.2")

    implementation ("androidx.navigation:navigation-fragment-ktx:2.5.0")
    implementation ("androidx.navigation:navigation-ui-ktx:2.5.0")

    implementation("androidx.room:room-common:2.6.0-beta01")
    implementation("androidx.room:room-ktx:2.6.0-beta01")
    kapt("androidx.room:room-compiler:2.6.0-beta01")

    testImplementation("androidx.room:room-testing:2.4.2")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    annotationProcessor ("androidx.room:room-compiler:2.4.2")

    testImplementation ("io.mockk:mockk:1.13.8")
    androidTestImplementation ("io.mockk:mockk-android:1.12.0")



}
