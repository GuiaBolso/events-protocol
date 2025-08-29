dependencies {
    // Projects
    api(project(":core"))
    testImplementation(project(":core").dependencyProject.sourceSets.test.get().output)
    testImplementation(project(":json-moshi"))
    testImplementation(project(":json-gson"))
    testImplementation(project(":json-jackson"))

    // Fuel
    compileOnly("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}
