// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.5.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
        classpath("com.google.gms:google-services:4.4.0")
        classpath("com.diffplug.spotless:spotless-plugin-gradle:6.22.0")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.5")

        // Add the Maven coordinates and latest version of the plugin
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}

plugins {
    id("com.google.dagger.hilt.android") version "2.52" apply false
}

allprojects {
    repositories {
//        google()
//        mavenCentral()
//        gradlePluginPortal()
    }
}