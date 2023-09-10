package br.com.guiabolso.tracing.utils.opentelemetry.coroutine

import io.opentelemetry.context.Context
import io.opentelemetry.context.ImplicitContextKeyed
import io.opentelemetry.context.Scope
import kotlinx.coroutines.ThreadContextElement
import kotlin.coroutines.CoroutineContext

data class SpanContextElement(private val otelContext: Context) : ThreadContextElement<Scope> {

    override val key = SpanContextElement

    override fun updateThreadContext(context: CoroutineContext): Scope {
        return otelContext.makeCurrent()
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: Scope) {
        oldState.close()
    }

    companion object Key : CoroutineContext.Key<SpanContextElement>
}

fun ImplicitContextKeyed.asContextElement(): CoroutineContext {
    return SpanContextElement(Context.current().with(this))
}
