dependencies {
    // Projects
    api(project(":core"))
    testImplementation(project(":core").dependencyProject.sourceSets.test.get().output)
    testImplementation(project(":json-parser-gson"))

    // Fuel
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
}
