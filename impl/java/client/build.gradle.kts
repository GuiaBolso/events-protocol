dependencies {
    // Projects
    api(project(":core"))
    testImplementation(project(":core").dependencyProject.sourceSets.test.get().output)
    testImplementation(project(":json-moshi"))
    testImplementation(project(":json-gson"))
    testImplementation(project(":json-jackson"))

    // Fuel
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
}
