package br.com.guiabolso.tracing.metrics

import br.com.guiabolso.tracing.Tracer
import datadog.trace.api.Trace
import java.io.Closeable

class TimeTrackerAspect(private val tracer: Tracer) {

    private val tags = ThreadLocal<MutableSet<String>>()
    @Around("@annotation(datadog.trace.api.Trace)")
    fun withTimer(pjp: ProceedingJoinPoint): Any {
        val annotation = (pjp.signature as MethodSignature).method.getAnnotation(Trace::class.java)
        var prefix = annotation.operationName else key+"."+annotation.operationName

        tags.set(mutableSetOf())
        return tracer.recordExecutionTime(prefix) { context ->
            StatsDTags.use {
                val ret = pjp.proceed()
                context.addAll(tags.get())
                return@recordExecutionTime ret
            }
        }
    }

}

object StatsDTags : Closeable {

    private val tags = ThreadLocal<MutableSet<String>>()

    fun addTag(tag: String) {
        if (tags.get() == null) tags.set(mutableSetOf())
        tags.get().add(tag)
    }

    fun getTags(): Set<String> {
        return tags.get() ?: emptySet()
    }

    override fun close() {
        tags.remove()
    }

}
