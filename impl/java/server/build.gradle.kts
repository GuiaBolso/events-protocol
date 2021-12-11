dependencies {
    // Projects
    api(project(":core"))
    api(project(":tracing"))
    testImplementation(project(":json-parser-gson"))
    testImplementation(project(":core").dependencyProject.sourceSets.test.get().output)
}
