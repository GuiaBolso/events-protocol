dependencies {
    // Projects
    api(project(":tracing"))
    
    testApi(project(":json-moshi"))

    // Kotest
    testImplementation("io.kotest:kotest-assertions-api:5.0.1")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.0.1")
    testImplementation("io.kotest:kotest-assertions-json-jvm:5.0.1")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.0.1")
}
