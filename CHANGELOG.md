# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased 3.0.0] 

### Warning

If your project have dependency with OpenTracing or DatadogOT you must upgrade its versions to `0.33.0` and `0.36.0` 
respectively.

### Added

- New standard event response for async executions.
- `EventException` as an abstraction of expected exceptions to be handled in the `ExceptionHandlerRegistry`.
- `EventException` is properly handled by the `ExceptionHandlerRegistry` out of the box. This behavior can be overwritten
registering one custom `EventExceptionHandler` in the `ExceptionHandlerRegistry`.
- Automatic nullable checks for `data classes` or classes annotated with `@Validatable`. **(Supported only in Kotlin classes)**
    - This validations checks if the object properties that are defined as not nullable have indeed some value
    avoiding NPE. E.g.: after serializing some Json to `data class` with a java library.
- `required` extension function for simple validations.
- New event handler abstraction `SecureTypedEventHandler` that automatically parses the event and validates the input and `userId`.
- New event handler abstraction `InsecureTypedEventHandler` that automatically parses the event and validates the input. 
**(This abstraction should be avoid at all costs. Always use `SecureTypedEventHandler` when you have the `userId`)**
- Adding event sunset property to `metadata` to warn clients to stop using it.
- Origin is now a comma+space separated list of systems, the left-most being the original client, and each successive 
system that process the request must add its on name. E.g: `Android, Kasbah, Ryzen, Hanamura`.

### Changed

- Fixing a bug parsing event error types that causes exception when handling `eventNotFound` and `badProtocol` responses.
- Updating Kotlin version from `1.3.41` to `1.3.50`.
- Updating OpenTracing version from `0.31.0` to `0.33.0`. **(Incompatible API changes)**
- Updating DatadogOT version from `0.30.0` to `0.36.0`. **(Incompatible API changes)**
- **`metadata`.`origin` is a required property to create a request event.**

### Removed

- Removing client pointless response `Response.Redirect`. Redirect are now treated as `Response.Success`.
- Removed `BypassedException` and `bypassExceptionHandler`. The same behavior can be accomplished with a custom `EventExceptionHandler`. 