plugins {
    alias(libs.plugins.android.application)
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("$rootDir/KEYSTORE.jks")
            storePassword = "INSERT_PASSWORD"
            keyAlias = "key0"
            keyPassword = "INSERT_PASSWORD"
        }
    }
    namespace = "com.example.harmony"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.harmony"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }


}

dependencies {
    implementation("com.google.android.gms:play-services-auth:21.1.1")
    implementation("com.spotify.android:auth:1.2.3")

    implementation ("androidx.browser:browser:1.7.0")
    implementation("androidx.webkit:webkit:1.8.0")  // WebView için
    implementation ("androidx.appcompat:appcompat:1.7.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.auth)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.media3.extractor)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}