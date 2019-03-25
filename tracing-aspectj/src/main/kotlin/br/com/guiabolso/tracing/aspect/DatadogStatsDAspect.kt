package br.com.guiabolso.tracing.aspect

import br.com.guiabolso.tracing.Tracer
import datadog.trace.api.Trace
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.reflect.MethodSignature
import java.io.Closeable

class DatadogStatsDAspect(private val tracer: Tracer) {

    @Around("@annotation(datadog.trace.api.Trace)")
    fun withTimer(pjp: ProceedingJoinPoint): Any {
        val annotation = (pjp.signature as MethodSignature).method.getAnnotation(Trace::class.java)

        val prefixAux = StatsDTags.get("prefix") ?: ""
        val prefix = if (prefixAux == "") annotation.operationName else prefixAux + "." + annotation.operationName

        return tracer.recordExecutionTime(prefix) { context ->
            StatsDTags.use {
                val ret = pjp.proceed()
                context.putAll(StatsDTags.getTags())
                StatsDTags.put("prefix", prefixAux)
                return@recordExecutionTime ret
            }
        }
    }

}

object StatsDTags : Closeable {

    private val tags = InheritableThreadLocal<MutableMap<String, String>>()

    fun addTag(key: String, value: String) {
        if (tags.get() == null) tags.set(mutableMapOf())
        tags.get()[key] = value
    }

    fun getTags(): Map<String, String> =
            tags.get()?.let { map ->
                map.filterNot { entry -> entry.key == "prefix" }
            } ?: emptyMap()

    fun get(key: String): String? = tags.get()[key]

    fun put(key: String, value: String) {
        tags.get()[key] = value
    }

    override fun close() {
        if (this.get("prefix") == "") tags.remove()
    }

}
