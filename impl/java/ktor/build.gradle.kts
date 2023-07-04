dependencies {
    // Projects
    api(project(":server"))
    testImplementation(project(":test"))

    testImplementation(project(":core").dependencyProject.sourceSets.test.get().output)
    testImplementation(project(":json-moshi"))
    testImplementation(project(":json-gson"))
    testImplementation(project(":json-jackson"))


    // Ktor
    implementation("io.ktor:ktor-server-core:2.2.4")
    testImplementation("io.ktor:ktor-server-tests:2.2.4")

    // Kotest
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.5.5")
}
