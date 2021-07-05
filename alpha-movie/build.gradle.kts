plugins {
    id("com.android.library")
    `maven-publish`
    signing
}

group = "tech.bam.alpha-movie"
version = "0.0.0-SNAPSHOT"

android {
    compileSdkVersion(30)
    buildToolsVersion("29.0.3")

    defaultConfig {
        minSdkVersion(18)
        targetSdkVersion(30)
        versionCode(1)
        versionName(version as String)
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

val emptyJavadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>(project.name) {
                groupId = group as String
                artifactId = project.name
                version = version
                from(components["release"])
            }
        }
    }

    extensions.findByType<PublishingExtension>()?.apply {
        repositories {
            maven {
                url = uri(
                    if (isReleaseBuild) {
                        "https://oss.sonatype.org/service/local/staging/deploy/maven2"
                    } else {
                        "https://oss.sonatype.org/content/repositories/snapshots"
                    }
                )
                credentials {
                    username = properties["sonatypeUsername"].toString()
                    password = properties["sonatypePassword"].toString()
                }
            }
        }

        publications.withType<MavenPublication>().configureEach {
            artifact(emptyJavadocJar.get())

            pom {
                name.set("alpha-movie")
                description.set("Improved alpha movie view for Android that plays alpha packed videos")
                url.set("https://github.com/bamlab/alpha-movie")

                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("bamlab")
                        name.set("Vincent Jouanne")
                    }
                }
                scm {
                    url.set("https://github.com/bamlab/alpha-movie")
                    connection.set("scm:git:https://github.com/bamlab/alpha-movie")
                }
            }
        }

        tasks.withType<Sign>().configureEach {
            onlyIf { isReleaseBuild }
        }

        extensions.findByType<SigningExtension>()?.apply {
            val publishing = extensions.findByType<PublishingExtension>() ?: return@apply
            val key = properties["signingKey"]?.toString()?.replace("\\n", "\n")
            val password = properties["signingPassword"]?.toString()

            useInMemoryPgpKeys(key, password)
            sign(publishing.publications)
        }
    }
}

val isReleaseBuild: Boolean
    get() = (properties["isReleaseBuild"] == "true")
