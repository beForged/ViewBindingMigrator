import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    antlr
    kotlin("jvm") version "1.5.10"
    application
    id("org.jmailen.kotlinter") version "3.5.0"
}

group = "me.scarlet"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    antlr("org.antlr:antlr4:4.8")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}


tasks.generateGrammarSource {
    maxHeapSize = "64m"

    arguments = arguments + listOf("-long-messages")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}