plugins {
    id("com.android.application")
    id("kotlin-android")
}
extra.apply {
    set("AMPLITUDE_API_KEY", if (project.hasProperty("AMPLITUDE_API_KEY")) project.property("AMPLITUDE_API_KEY") as String else "")
    set("EXPERIMENT_API_KEY", "")
}

android {
    namespace = "com.amplitude.android.sample"

    compileSdk = 34

    defaultConfig {
        applicationId = "com.amplitude.android.sample"
        minSdk = 19
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        buildConfigField("String", "AMPLITUDE_API_KEY", "\"${extra["AMPLITUDE_API_KEY"]}\"")
        buildConfigField("String", "EXPERIMENT_API_KEY", "\"${extra["EXPERIMENT_API_KEY"]}\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    lint {
        abortOnError = false
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":android"))
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("com.google.android.gms:play-services-ads:20.6.0")
    implementation("com.google.android.gms:play-services-appset:16.0.2")
    implementation("com.amplitude:experiment-android-client:1.6.3")
    // For trouble shooting plugin
    implementation("com.google.code.gson:gson:2.10")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}

