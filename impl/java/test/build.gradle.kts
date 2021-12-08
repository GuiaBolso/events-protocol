dependencies {
    // Projects
    api(project(":core"))
    api(project(":server"))

    // Kotest
    api("io.kotest:kotest-assertions-api:5.0.1")
    api("io.kotest:kotest-assertions-core-jvm:5.0.1")
    implementation("io.kotest:kotest-assertions-json-jvm:5.0.1")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.0.1")
}
