dependencies {
    api(project(":core"))

    // Moshi
    api("com.squareup.moshi:moshi:1.14.0")
    api("com.squareup.moshi:moshi-kotlin:1.14.0")
    api("com.squareup.moshi:moshi-adapters:1.14.0")

    // Kotest
    testImplementation("io.kotest:kotest-assertions-api:5.5.5")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.5")
    testImplementation("io.kotest:kotest-assertions-json-jvm:5.5.5")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.5.5")
}
