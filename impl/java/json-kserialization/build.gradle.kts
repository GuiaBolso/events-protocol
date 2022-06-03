import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("plugin.serialization") version "1.6.21"
}

dependencies {
    api(project(":core"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    // Kotest
    testImplementation("io.kotest:kotest-assertions-api:5.0.1")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.0.1")
    testImplementation("io.kotest:kotest-assertions-json-jvm:5.0.1")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.0.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        this.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
}
