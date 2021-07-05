plugins {
    id("com.android.library")
}

android {
    compileSdkVersion(30)
    buildToolsVersion("25.0.3")

    defaultConfig {
        minSdkVersion(18)
        targetSdkVersion(30)
        versionCode(1)
        versionName(rootProject.ext.get("version") as String?)
    }
    buildTypes {
        getByName("release") {
            minifyEnabled(false)
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}