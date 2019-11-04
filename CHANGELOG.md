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

### Changed

- Fixing a bug parsing event error types that causes exception when handling `eventNotFound` and `badProtocol` responses.
- Updating Kotlin version from `1.3.41` to `1.3.50`.
- Updating OpenTracing version from `0.31.0` to `0.33.0`. **(Incompatible API changes)**
- Updating DatadogOT version from `0.30.0` to `0.36.0`. **(Incompatible API changes)**

### Removed

- Removing client pointless response `Response.Redirect`. Redirect are now treated as `Response.Success`.