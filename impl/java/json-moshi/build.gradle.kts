dependencies {
    api(project(":core"))

    // Moshi
    implementation("com.squareup.moshi:moshi:1.12.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")
    implementation("com.squareup.moshi:moshi-adapters:1.12.0")


    testImplementation(project(":core").dependencyProject.sourceSets.test.get().output)
}
