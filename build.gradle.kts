import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30"
    application
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    id("org.jlleitschuh.gradle.ktlint-idea") version "10.2.0"
}

group = "fr.spacefox"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {

    implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect", version = "1.5.30")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = "1.5.2")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-reactor", version = "1.5.2")

    implementation(group = "io.insert-koin", name = "koin-core", version = "3.1.2")

    implementation(group = "com.discord4j", name = "discord4j-core", version = "3.1.7")

    implementation(group = "org.apache.lucene", name = "lucene-core", version = "8.9.0")
    implementation(group = "org.apache.lucene", name = "lucene-queryparser", version = "8.9.0")

    implementation(group = "com.fasterxml.jackson.core", name = "jackson-core", version = "2.12.5")
    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = "2.12.5")
    implementation(group = "com.fasterxml.jackson.datatype", name = "jackson-datatype-jsr310", version = "2.12.5")

    implementation(group = "org.apache.logging.log4j", name = "log4j-api", version = "2.14.1")
    implementation(group = "org.apache.logging.log4j", name = "log4j-core", version = "2.14.1")
    implementation(group = "org.apache.logging.log4j", name = "log4j-slf4j-impl", version = "2.14.1")

    testImplementation(kotlin("test"))
    implementation(group = "io.insert-koin", name = "koin-test", version = "3.1.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("fr.spacefox.calvinandbot.application.CalvinandbotApplicationKt")
}
