package br.com.guiabolso.tracing.engine.opentelemetry

import io.opentelemetry.api.trace.Span
import io.opentelemetry.context.ContextKey

internal object SpanContextKey {
    internal val KEY = ContextKey.named<Span>("opentelemetry-trace-span-key")
}
