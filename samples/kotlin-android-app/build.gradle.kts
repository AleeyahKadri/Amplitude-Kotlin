plugins {
    id("com.android.application")
    id("kotlin-android")
}

val amplitudeApiKey = if (project.hasProperty("AMPLITUDE_API_KEY")) {
    project.property("AMPLITUDE_API_KEY") as String
} else {
    ""
}
val experimentApiKey = ""

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
        buildConfigField("String", "AMPLITUDE_API_KEY", "\"$amplitudeApiKey\"")
        buildConfigField("String", "EXPERIMENT_API_KEY", "\"$experimentApiKey\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
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
    implementation(group = "com.google.code.gson", name = "gson", version = "2.10")
    implementation(group = "com.squareup.okhttp3", name = "okhttp", version = "4.12.0")
}
