plugins {
    id("com.android.application")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.android")
}

android {

    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    namespace = "com.doctoror.particleswallpaper"

    defaultConfig {
        applicationId = "com.doctoror.particleswallpaper"

        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()

        versionCode = 42
        versionName = "2.3.2"

        resourceConfigurations += listOf("en", "ru", "uk")
    }

    packaging {
        resources.excludes.add("META-INF/rxjava.properties")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    signingConfigs {

        create("release") {
            storeFile = file("../keystore/upload.jks")
        }
    }

    buildTypes {

        getByName("debug") {
        }

        getByName("release") {
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")

            if (project.hasProperty("keyAlias")) {
                signingConfig!!.keyAlias = project.property("keyAlias").toString()
            }

            if (project.hasProperty("storePassword")) {
                signingConfig!!.storePassword = project.property("storePassword").toString()
            }

            if (project.hasProperty("keyPassword")) {
                signingConfig!!.keyPassword = project.property("keyPassword").toString()
            }

            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-glide.pro",
                "proguard-lifecycle.pro",
                "proguard-rx-java.pro"
            )
        }
    }

    lint {
        checkAllWarnings = true
        disable.add("AppCompatResource")
    }
}

dependencies {
    testImplementation(libs.test.core)
    testImplementation(libs.junit.vintage)
    testImplementation(libs.koin.test)
    testImplementation(libs.kotlin.reflect)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.opengl.android)
    testImplementation(libs.robolectric)

    implementation(libs.koin.android)
    implementation(libs.rxJava)
    implementation(libs.rxAndroid)

    implementation(files("libs/library-exposed-release.aar"))
    implementation(files("libs/opengl-exposed-release.aar"))
    implementation(libs.color.picker)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle)

    implementation(libs.licensesdialog) {
        exclude(group = "androidx.appcompat", module = "appcompat")
        exclude(group = "com.android.support", module = "appcompat-v7")
    }

    implementation(libs.glide)
    ksp(libs.glide.ksp)
}
