// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("io.codearte.nexus-staging") version "0.22.0"
}

group = "tech.bam"

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.3")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

nexusStaging {
    username = properties["sonatypeUsername"].toString()
    password = properties["sonatypePassword"].toString()
}
