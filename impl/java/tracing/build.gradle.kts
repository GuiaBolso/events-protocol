dependencies {
    // Datadog
    api("com.datadoghq:dd-trace-ot:1.11.2")
    api("com.datadoghq:dd-trace-api:1.11.2")
    implementation("com.datadoghq:java-dogstatsd-client:4.2.0")
    
    // Open Tracing
    api("io.opentracing:opentracing-api:0.33.0")
    api("io.opentracing:opentracing-util:0.33.0")

    // Open Telemetry
    api("io.opentelemetry:opentelemetry-api:1.27.0")
    
    // AspectJ
    implementation("org.aspectj:aspectjweaver:1.9.19")
}
