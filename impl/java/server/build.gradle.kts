dependencies {
    // Projects
    api(project(":core"))
    api(project(":tracing"))
    testImplementation(project(":core").dependencyProject.sourceSets.test.get().output)
}