import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.org.fusesource.jansi.AnsiRenderer.test
import java.net.URI

plugins {
    kotlin("jvm") version "1.3.0"
}

group = "net.lulab.dev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven {
        setUrl("http://dl.bintray.com/dnvriend/maven")
    }
}

val scalaVersion = "2.12"
val akkaVersion = "2.5.15"
val fuelVersion = "1.16.0"

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("com.typesafe.akka:akka-actor_$scalaVersion:$akkaVersion")
    testCompile("com.typesafe.akka:akka-testkit_$scalaVersion:$akkaVersion")
    compile("com.typesafe.akka:akka-persistence_$scalaVersion:$akkaVersion")
    testCompile("com.github.dnvriend:akka-persistence-inmemory_$scalaVersion:$akkaVersion.1")
    compile("com.typesafe.akka:akka-stream_$scalaVersion:$akkaVersion")
    compile("com.github.kittinunf.fuel:fuel:$fuelVersion")
    compile("com.github.kittinunf.fuel:fuel-gson:$fuelVersion")
    compile("com.github.kittinunf.fuel:fuel-coroutines:$fuelVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}