
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
   
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
 namespace = "com.kenkenta.kosodatemamanavi"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kenkenta.kosodatemamanavi"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
  signingConfigs {

        create("release") {
            storeFile = file("kosodate-upload.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
}
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
    }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
}
