plugins {
    //alias(libs.plugins.android.application)
    id("com.android.application")
}

android {
    namespace = "com.example.rentalpropertyapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.rentalpropertyapp"
        minSdk = 21
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

    buildFeatures{
        viewBinding = true
    }
}


dependencies {
    implementation(libs.glide)
    implementation(libs.swiperefreshlayout)
    annotationProcessor(libs.glide.compiler)
    implementation(libs.cardview)
    implementation(libs.play.services.cast.framework)
    annotationProcessor(libs.glide.compiler)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.volley)
    implementation(libs.circleimageview)
    implementation(libs.imagepicker)
    implementation(libs.mpandroidchart)  // Added MPAndroidChart dependency
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    implementation(libs.uploadservice)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

}