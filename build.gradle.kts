import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30"
    application
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    id("org.jlleitschuh.gradle.ktlint-idea") version "10.2.0"
    jacoco
    idea
}

group = "fr.spacefox"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {

    val kotlinVersion = "1.5.30"
    val kotlinxCoroutinesVersion = "1.5.2"
    val koinVersion = "3.1.2"
    val discord4jVersion = "3.1.7"
    val luceneVersion = "8.9.0"
    val jacksonVersion = "2.12.5"
    val log4jVersion = "2.14.1"
    val mockkVersion = "1.12.0"

    implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect", version = kotlinVersion)
    implementation(
        group = "org.jetbrains.kotlinx",
        name = "kotlinx-coroutines-core",
        version = kotlinxCoroutinesVersion
    )
    implementation(
        group = "org.jetbrains.kotlinx",
        name = "kotlinx-coroutines-reactor",
        version = kotlinxCoroutinesVersion
    )

    implementation(group = "io.insert-koin", name = "koin-core", version = koinVersion)

    implementation(group = "com.discord4j", name = "discord4j-core", version = discord4jVersion)

    implementation(group = "org.apache.lucene", name = "lucene-core", version = luceneVersion)
    implementation(group = "org.apache.lucene", name = "lucene-queryparser", version = luceneVersion)

    implementation(group = "com.fasterxml.jackson.core", name = "jackson-core", version = jacksonVersion)
    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = jacksonVersion)
    implementation(group = "com.fasterxml.jackson.datatype", name = "jackson-datatype-jsr310", version = jacksonVersion)

    implementation(group = "org.apache.logging.log4j", name = "log4j-api", version = log4jVersion)
    implementation(group = "org.apache.logging.log4j", name = "log4j-core", version = log4jVersion)
    implementation(group = "org.apache.logging.log4j", name = "log4j-slf4j-impl", version = log4jVersion)

    testImplementation(
        group = "org.jetbrains.kotlinx",
        name = "kotlinx-coroutines-test",
        version = kotlinxCoroutinesVersion
    )

    testImplementation(group = "io.insert-koin", name = "koin-test", version = koinVersion)
    testImplementation(group = "io.insert-koin", name = "koin-test-junit5", version = koinVersion)

    testImplementation(group = "io.mockk", name = "mockk", version = mockkVersion)
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.5".toBigDecimal()
            }
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("fr.spacefox.calvinandbot.application.CalvinandbotApplicationKt")
}
