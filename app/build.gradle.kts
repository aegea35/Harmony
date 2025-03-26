plugins {
    alias(libs.plugins.android.application)
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("/Users/yankiozan/Desktop/Harmony2 2/KEYSTORE.jks")
            storePassword = "GayEge123!"
            keyAlias = "key0"
            keyPassword = "GayEge123!"
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

    implementation(libs.core)
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")

    androidTestImplementation ("androidx.test:runner:1.4.0")
    androidTestImplementation ("androidx.test:rules:1.4.0")
    androidTestImplementation ("androidx.test.espresso:espresso-intents:3.5.1") // Intent testi için
    androidTestImplementation ("com.squareup.okhttp3:mockwebserver:4.11.0") // API testi için

    testImplementation ("org.powermock:powermock-module-junit4:2.0.9")
    testImplementation ("org.powermock:powermock-api-mockito2:2.0.9")
    testImplementation ("org.mockito:mockito-core:5.3.1")

    testImplementation ("io.mockk:mockk:1.13.5")
//    testImplementation ("org.mockito:mockito-inline:5.3.1")

    androidTestImplementation ("org.mockito:mockito-android:5.3.1")
    testImplementation ("com.google.truth:truth:1.1.3")
    androidTestImplementation ("com.google.truth:truth:1.1.3")






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