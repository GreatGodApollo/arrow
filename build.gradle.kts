import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    `java-library`
    `maven-publish`

    kotlin("jvm") version "1.7.10"
    id("com.github.gmazzo.buildconfig") version "3.1.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "xyz.brettb"
version = "${rootProject.property("major")}.${rootProject.property("minor")}.${rootProject.property("patch")}"

val commit = runCommand(arrayListOf("git", "rev-parse", "HEAD"))

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    listOf("stdlib-jdk16", "reflect").forEach { implementation(kotlin(it)) }

    compileOnly("org.bukkit:bukkit:")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "16"

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

tasks {
    withType<ProcessResources> {
        filesMatching("*.yml") {
            expand(project.properties)
        }
    }
}

publishing {
    repositories {
        maven {
            name = "internal.repo"
            url = uri("$path/../../maven-repo")
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
        }
    }
}

fun runCommand(commands: List<String>): String {
    val stdout = org.apache.commons.io.output.ByteArrayOutputStream()
    exec {
        commandLine = commands
        standardOutput = stdout
    }
    return stdout.toString("utf-8").trim()
}