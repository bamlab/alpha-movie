// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    `maven-publish`
    id("io.codearte.nexus-staging") version "0.22.0"
    signing
}

group = "tech.bam"
version = "0.0.0-SNAPSHOT"

val emptyJavadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

ext {
    set("version", version)
}

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


afterEvaluate {
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

nexusStaging {
    username = properties["sonatypeUsername"].toString()
    password = properties["sonatypePassword"].toString()
}


val isReleaseBuild: Boolean
    get() = (properties["isReleaseBuild"] == "true")
