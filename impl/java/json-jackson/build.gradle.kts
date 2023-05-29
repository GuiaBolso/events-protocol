dependencies {
    api(project(":core"))

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core
    api("com.fasterxml.jackson.core:jackson-databind:2.15.1")

    // Kotest
    testImplementation("io.kotest:kotest-assertions-api:5.5.5")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.5")
    testImplementation("io.kotest:kotest-assertions-json-jvm:5.5.5")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.5.5")
}
