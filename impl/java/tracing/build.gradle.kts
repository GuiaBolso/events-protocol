dependencies {
    // Datadog
    api("com.datadoghq:dd-trace-ot:0.90.0")
    api("com.datadoghq:dd-trace-api:0.90.0")
    implementation("com.datadoghq:java-dogstatsd-client:3.0.0")
    
    // Open Tracing
    api("io.opentracing:opentracing-api:0.33.0")
    api("io.opentracing:opentracing-util:0.33.0")

    // Open Telemetry
    api("io.opentelemetry:opentelemetry-api:1.28.0")
    api("io.opentelemetry:opentelemetry-extension-kotlin:1.28.0")

    // AspectJ
    implementation("org.aspectj:aspectjweaver:1.9.7")
}
