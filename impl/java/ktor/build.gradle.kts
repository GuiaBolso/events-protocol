dependencies {
    // Projects
    api(project(":server"))
    testImplementation(project(":test"))
    testImplementation(project(":json-moshi"))

    // Ktor
    implementation("io.ktor:ktor-server-core:1.6.8")
    testImplementation("io.ktor:ktor-server-tests:1.6.8")
    
    // Kotest
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.5.5")
    testImplementation("io.kotest:kotest-assertions-ktor:4.4.3")
}
