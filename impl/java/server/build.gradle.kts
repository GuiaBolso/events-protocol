dependencies {
    // Projects
    api(project(":core"))
    api(project(":tracing"))
    testImplementation(project(":json-moshi"))
    testImplementation(project(":core").dependencyProject.sourceSets.test.get().output)
}
