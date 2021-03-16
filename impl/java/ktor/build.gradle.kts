dependencies {
    // Projects
    api(project(":server"))
    testImplementation(project(":test"))
    
    // Ktor
    implementation("io.ktor:ktor-server-core:1.5.1")
    testImplementation("io.ktor:ktor-server-tests:1.5.1")
    
    // Kotest
    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.2.3") {
        exclude(group = "org.junit.jupiter")
        exclude(group = "org.junit.platform")
    }
    testImplementation("io.kotest:kotest-assertions-ktor:4.2.3")
}