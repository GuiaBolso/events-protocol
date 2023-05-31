dependencies {
    // Projects
    api(project(":core"))
    api(project(":tracing"))
    testImplementation(project(":json-moshi"))
    testImplementation(project(":json-gson"))
    testImplementation(project(":json-jackson"))
    testImplementation(project(":core").dependencyProject.sourceSets.test.get().output)
}
