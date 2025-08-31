plugins {
    alias(libs.plugins.android.application)
}
subprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}


android {
    namespace = "com.example.studentattandance"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.studentattandance"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    // Core Android dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    
    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    
    // UI Components
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    implementation(libs.swiperefreshlayout)
    implementation(libs.viewpager2)
    implementation(libs.circleimageview)
    implementation(libs.shimmer)
    
    // Architecture Components
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.room.ktx)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.runtime)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    
    // Additional Room dependencies
    implementation("androidx.room:room-common:2.6.1")
    implementation("androidx.room:room-migration:2.6.1")
    implementation("androidx.room:room-testing:2.6.1")
    
    // Charts (temporarily disabled)
    // implementation(libs.mpandroidchart)
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}