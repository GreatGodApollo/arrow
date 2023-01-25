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

buildConfig {
    packageName("xyz.brettb.arrow")
    className("ArrowInfo")
    buildConfigField("String", "VERSION", "\"${version}\"")
    buildConfigField("String", "COMMIT", "\"$commit\"")
    buildConfigField("long", "BUILD_TIME", "${System.currentTimeMillis()}L")
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://rayzr.dev/repo/")
}

dependencies {
    listOf("stdlib-jdk8", "reflect").forEach { api(kotlin(it)) }

    api("com.github.cryptomorin:XSeries:9.1.0") {
        isTransitive = false
    }
    api("me.ialistannen:MiniNBT:1.0.2")
    compileOnly("org.bukkit:bukkit:1.15.2-R0.1-SNAPSHOT")
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
            name = "brettbRepo"
            url = uri("https://repo.brettb.xyz/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "xyz.brettb"
            artifactId = "arrow"
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