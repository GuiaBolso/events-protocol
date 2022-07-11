dependencies {
    // Projects
    api(project(":core"))
    testImplementation(project(":core").dependencyProject.sourceSets.test.get().output)
    testImplementation(project(":json-moshi"))

    // Fuel
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
}
