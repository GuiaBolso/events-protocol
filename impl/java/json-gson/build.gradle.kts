dependencies {
    api(project(":core"))

    api("com.google.code.gson:gson:2.9.0")

    // Kotest
    testImplementation("io.kotest:kotest-assertions-api:5.0.1")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.0.1")
    testImplementation("io.kotest:kotest-assertions-json-jvm:5.0.1")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.0.1")
}
