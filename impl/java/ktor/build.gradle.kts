dependencies {
    // Projects
    api(project(":server"))
    testImplementation(project(":test"))
    testImplementation(project(":json-moshi"))

    // Ktor
    implementation("io.ktor:ktor-server-core:2.2.4")
    testImplementation("io.ktor:ktor-server-tests:2.2.4")

    // Kotest
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.5.5")
}
