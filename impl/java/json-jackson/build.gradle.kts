dependencies {
    api(project(":core"))

    api("com.fasterxml.jackson.core:jackson-databind:2.14.0")

    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.0")

    // Kotest
    testImplementation("io.kotest:kotest-assertions-api:5.5.5")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.5")
    testImplementation("io.kotest:kotest-assertions-json-jvm:5.5.5")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.5.5")
}
