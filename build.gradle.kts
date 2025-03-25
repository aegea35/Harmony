// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
}
buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.4") // Güncel Gradle sürümü
        repositories {
            google()
            mavenCentral()
        }

    }
}