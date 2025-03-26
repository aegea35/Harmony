// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
}
buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.4") // Güncel Gradle sürümü
        classpath("com.google.gms:google-services:4.2.0")  // or the latest version
        repositories {
            google()
            mavenCentral()
        }

    }
}