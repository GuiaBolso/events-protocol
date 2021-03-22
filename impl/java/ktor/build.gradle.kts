dependencies {
    // Projects
    api(project(":server"))
    testImplementation(project(":test"))
    
    // Ktor
    implementation("io.ktor:ktor-server-core:1.5.1")
    testImplementation("io.ktor:ktor-server-tests:1.5.1")
    
    // Kotest
    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.4.3") 
    testImplementation("io.kotest:kotest-assertions-ktor:4.4.3")
}