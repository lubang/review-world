import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    base
    idea
    kotlin("jvm") version "1.3.10" apply false
}

subprojects {
    group = "com.github.lubang.review.world"
    version = "0.1"
}