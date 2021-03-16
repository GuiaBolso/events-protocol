dependencies {
    // Projects
    api(project(":core"))
    api(project(":server"))

    // Kotest
    api("io.kotest:kotest-assertions-api:4.2.3")
    api("io.kotest:kotest-assertions-core-jvm:4.2.3")
    implementation("io.kotest:kotest-assertions-json-jvm:4.2.3")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.2.3")
}