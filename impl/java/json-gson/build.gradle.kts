dependencies {
    api(project(":core"))

    api("com.google.code.gson:gson:2.10.1")

    // Kotest
    testImplementation("io.kotest:kotest-assertions-api:5.5.5")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.5")
    testImplementation("io.kotest:kotest-assertions-json-jvm:5.5.5")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.5.5")
}
