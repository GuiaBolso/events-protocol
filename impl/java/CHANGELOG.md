# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased] 

## Warning
There are several breaking changes in this version caused by the replacement of the core data structure used to support the json types, and the abstraction introduced to support others json parse implementations.
In this version, you MUST pick up an implementation of [JsonAdapter](/core/src/main/kotlin/br/com/guiabolso/events/json/JsonAdapter.kt) api. Currently there are available
implementation for [moshi](https://github.com/square/moshi), [br.com.guiabolso:events-json-moshi](json-moshi/) and [br.com.guiabolso:events-json-gson](json-gson/).

### Added
- Abstraction for json parsers, now this library don`t have a hard dependency with Gson anymore.
- Tree data structure to represent json
- New module events-json-moshi to support [moshi](https://github.com/square/moshi) as the json parser.

### Removed
- Dependency with Gson data structure, JsonElement, JsonObject etc

## 4.0.0

### Warning

Changes in dependencies:
- `io.opentracing:opentracing-api` is now set to `api`. Defining this dependency explicit will not be required anymore.
- `io.opentracing:opentracing-util` is now set to `api`. Defining this dependency explicit will not be required anymore.
- `com.datadoghq:dd-trace-ot` is now set to `api`. Defining this dependency explicit will not be required anymore.
- `com.datadoghq:dd-trace-api` is now set to `api`. Defining this dependency explicit will not be required anymore.

**If you use the datadog with StatsD you need to explicitly create a Tracer with `withDatadogAPMAndStatsD(prefix: String, host: String, port: Int)`.**

### Added

- Now you can use the [Tracer](tracing/src/main/kotlin/br/com/guiabolso/tracing/Tracer.kt) to wrap instances of
`ExecutorService` and `ScheduledExecutorService` to keep tracking of the execution when switching threads.

### Changed

- New way of building a tracer with [TracerBuilder](tracing/src/main/kotlin/br/com/guiabolso/tracing/builder/TracerBuilder.kt).
- The default tracer can now be found at [DefaultTracer](core/src/main/kotlin/br/com/guiabolso/events/tracer/DefaultTracer.kt).
This tracer is configured so that it can wrap `ExecutorService` and `ScheduledExecutorService` to propagate the `event.id`
and `event.flowId` when switching threads.

### Removed

- Removing tracer method `executeAsync`. Now use the `wrap` method on the `ExecutorService` to get an enhanced instance
that can keep tracking of the execution when switching threads.
- `TracerFactory` removed, use the [TracerBuilder](tracing/src/main/kotlin/br/com/guiabolso/tracing/builder/TracerBuilder.kt) or
the [DefaultTracer](core/src/main/kotlin/br/com/guiabolso/events/tracer/DefaultTracer.kt) instead.
- `TracingExecutorServiceWrapper` removed, use `tracer.wrap` instead.
- `EventContextExecutorServiceWrapper` removed, use `tracer.wrap` instead. Note that the tracer must be build with 
`.withContextManager(EventThreadContextManager)` to keep its behavior.
- Removing unnecessary 'SAMLambdaEventProcessor'.
