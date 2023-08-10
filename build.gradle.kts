import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

description = "FunnyCommands|Parent"

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "signing")
    apply(plugin = "maven-publish")

    group = "net.dzikoysk"
    version = "0.7.0"

    repositories {
        mavenCentral()
        maven {
            name = "panda-repository"
            url = uri("https://repo.panda-lang.org/releases")
        }
        maven {
            name = "spigot-repository"
            url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        }
        maven {
            name = "sonatype-repository"
            url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        }
    }

    publishing {
        repositories {
            maven {
                name = "panda-repository"
                url = uri("https://maven.reposilite.com/${if (version.toString().endsWith("-SNAPSHOT")) "snapshots" else "releases"}")

                credentials {
                    username = getEnvOrProperty("MAVEN_NAME", "mavenUser")
                    password = getEnvOrProperty("MAVEN_TOKEN", "mavenPassword")
                }
            }
        }
    }

    afterEvaluate {
        description
            ?.takeIf { it.isNotEmpty() }
            ?.split("|")
            ?.let { (projectName, projectDescription) ->
                publishing {
                    publications {
                        create<MavenPublication>("library") {
                            pom {
                                name.set(projectName)
                                description.set(projectDescription)
                                url.set("https://github.com/FunnyGuilds/FunnyCommands")

                                licenses {
                                    license {
                                        name.set("Apache-2.0 license")
                                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                                    }
                                }
                                developers {
                                    developer {
                                        id.set("dzikoysk")
                                        name.set("dzikoysk")
                                        email.set("dzikoysk@dzikoysk.net")
                                    }
                                    developer {
                                        id.set("peridot")
                                        name.set("Peridot")
                                        email.set("peridot491@pm.me")
                                    }
                                }
                                scm {
                                    connection.set("scm:git:git://github.com/FunnyGuilds/FunnyCommands.git")
                                    developerConnection.set("scm:git:ssh://github.com/FunnyGuilds/FunnyCommands.git")
                                    url.set("https://github.com/FunnyGuilds/FunnyCommands.git")
                                }
                            }

                            from(components.getByName("java"))
                        }
                    }
                }
            }
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

subprojects {
    java {
        withJavadocJar()
        withSourcesJar()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        setForkEvery(1)
        maxParallelForks = 4

        testLogging {
            events(TestLogEvent.STARTED, TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
            showStandardStreams = true
        }
    }
}

fun getEnvOrProperty(env: String, property: String): String? =
    System.getenv(env) ?: findProperty(property)?.toString()