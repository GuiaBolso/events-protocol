dependencies {
    // Datadog
    api("com.datadoghq:dd-trace-ot:0.82.0")
    api("com.datadoghq:dd-trace-api:0.82.0")
    implementation("com.datadoghq:java-dogstatsd-client:2.9.0")
    
    // Open Tracing
    api("io.opentracing:opentracing-api:0.32.0")
    api("io.opentracing:opentracing-util:0.32.0")
    
    // AspectJ
    implementation("org.aspectj:aspectjweaver:1.9.1")
}