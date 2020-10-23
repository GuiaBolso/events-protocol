package br.com.guiabolso.tracing.isolated

import io.opentracing.Scope
import io.opentracing.ScopeManager
import io.opentracing.Span
import io.opentracing.Tracer
import io.opentracing.util.ThreadLocalScopeManager

/**
 * Tracer that isolates the spans in a separated scopeManager
 */
class IsolatedTracer(tracer: Tracer) : Tracer by tracer {

    private val scopeManager = ThreadLocalScopeManager()

    override fun scopeManager(): ScopeManager {
        return scopeManager
    }

    override fun activateSpan(span: Span): Scope {
        return scopeManager().activate(span)
    }

    override fun activeSpan(): Span? {
        return scopeManager().activeSpan()
    }
}
