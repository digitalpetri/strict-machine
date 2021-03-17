import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    kotlin("jvm") version "1.4.30"
    `maven-publish`
    signing
}

group = "com.digitalpetri.fsm"
version = "0.4-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // BYO SLF4J
    compileOnly("org.slf4j:slf4j-api:1.7.+")

    testImplementation(kotlin("stdlib", "1.3.10"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")
    testRuntimeOnly("org.slf4j:slf4j-simple:1.7.25")
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<Test> {
        useJUnitPlatform()

        testLogging {
            events("PASSED", "FAILED", "SKIPPED")
        }
    }
}

task<Jar>("sourcesJar") {
    from(sourceSets.main.get().allJava)
    classifier = "sources"
}

task<Jar>("javadocJar") {
    from(tasks.javadoc)
    classifier = "javadoc"
}

tasks.withType<Jar> {
    manifest {
        attributes("Automatic-Module-Name" to "com.digitalpetri.strictmachine")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "strict-machine"

            from(components["java"])

            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])

            pom {
                name.set("Strict Machine")
                description.set("A declarative DSL for building asynchronously evaluated Finite State Machines on the JVM")
                url.set("https://github.com/digitalpetri/strict-machine")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("kevinherron")
                        name.set("Kevin Herron")
                        email.set("kevinherron@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com.com/digitalpetri/strict-machine.git")
                    developerConnection.set("scm:git:ssh://github.com.com/digitalpetri/strict-machine.git")
                    url.set("https://github.com/digitalpetri/strict-machine")
                }
            }
        }
    }

    repositories {
        maven {
            credentials {
                username = project.findProperty("ossrhUsername") as String?
                password = project.findProperty("ossrhPassword") as String?
            }

            // change URLs to point to your repos, e.g. http://my.org/repo
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
        }

        mavenLocal()
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["mavenJava"])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
