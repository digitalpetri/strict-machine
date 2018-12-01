import org.gradle.kotlin.dsl.support.KotlinPluginsBlock
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.10"
    `maven-publish`
}

group = "com.digitalpetri.fsm"
version = "0.1-SNAPSHOT"

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

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets["main"].allSource)
}

publishing {
    repositories {
        mavenLocal()
    }

    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar.get())
        }
    }
}
