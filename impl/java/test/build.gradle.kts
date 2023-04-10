dependencies {
    // Projects
    api(project(":core"))
    api(project(":server"))
    testImplementation(project(":json-moshi"))

    // Kotest
    api("io.kotest:kotest-assertions-api:5.5.5")
    api("io.kotest:kotest-assertions-core-jvm:5.5.5")
    implementation("io.kotest:kotest-assertions-json-jvm:5.5.5")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.5.5")
}
