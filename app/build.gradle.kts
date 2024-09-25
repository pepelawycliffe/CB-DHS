plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
//    id("com.google.dagger.hilt.android")
//    alias(libs.plugins.google.gms.google.services)
//    id("com.google.firebase.crashlytics")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.cb_dhs"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cb_dhs"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
//        sourceCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true

    }
//    kotlinOptions {
//        jvmTarget = "1.8"
//    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources.excludes.addAll(
            listOf(
                "META-INF/ASL-2.0.txt",
                "META-INF/LGPL-3.0.txt",
                "META-INF/LICENSE.md",
                "META-INF/NOTICE.md",
                "META-INF/sun-jaxb.episode",
                "META-INF/DEPENDENCIES"
            )
        )
    }
    kotlin { jvmToolchain(11) }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.runtime.livedata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
//..............................................fhir.....................................
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.activity:activity-ktx:1.9.1")
    implementation("androidx.compose.runtime:runtime-android:1.7.1")
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    testImplementation("org.testng:testng:6.9.6")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    implementation("androidx.activity:activity-ktx:1.9.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.fragment:fragment-ktx:1.8.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.work:work-runtime-ktx:2.9.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.20")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("com.google.android.fhir:engine:1.0.0")
    implementation("com.google.android.fhir:data-capture:1.1.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.5.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.6.0")

//    ........................................end fhir...................................
    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.44")
    implementation("com.google.android.gms:play-services-auth:20.6.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

//    kapt("com.google.dagger:hilt-android-compiler:2.44")

}

