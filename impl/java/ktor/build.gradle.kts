dependencies {
    // Projects
    api(project(":server"))
    testImplementation(project(":test"))
    testImplementation(project(":json-parser-gson"))
    testImplementation(project(":core").dependencyProject.sourceSets.test.get().output)

    // Ktor
    implementation("io.ktor:ktor-server-core:1.6.7")
    testImplementation("io.ktor:ktor-server-tests:1.6.7")
    
    // Kotest
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.0.1")
    testImplementation("io.kotest:kotest-assertions-ktor:4.4.3")
}
