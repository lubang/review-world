import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
}

repositories {
    mavenCentral()
    jcenter()
    maven {
        setUrl("http://dl.bintray.com/dnvriend/maven")
    }
}

val scalaVersion = "2.12"
val akkaVersion = "2.5.15"
val akkaHttpVersion = "10.1.5"
val fuelVersion = "1.16.0"

dependencies {
    compile(kotlin("stdlib-jdk8"))

    compile(project(":review-world-core"))

    compile("com.typesafe.akka:akka-actor_$scalaVersion:$akkaVersion")
    testCompile("com.typesafe.akka:akka-testkit_$scalaVersion:$akkaVersion")
    compile("com.typesafe.akka:akka-persistence_$scalaVersion:$akkaVersion")
    compile("org.fusesource.leveldbjni:leveldbjni-all:1.8")
    testCompile("com.github.dnvriend:akka-persistence-inmemory_$scalaVersion:$akkaVersion.1")
    compile("com.typesafe.akka:akka-stream_$scalaVersion:$akkaVersion")
    compile("com.typesafe.akka:akka-http_$scalaVersion:$akkaHttpVersion")

    compile("com.github.kittinunf.fuel:fuel:$fuelVersion")
    compile("com.github.kittinunf.fuel:fuel-gson:$fuelVersion")
    compile("com.github.kittinunf.fuel:fuel-coroutines:$fuelVersion")

    compile("com.google.guava:guava:27.0.1-jre")
    compile("com.google.inject:guice:4.2.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}