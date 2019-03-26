package br.com.guiabolso.tracing.aspect

import br.com.guiabolso.tracing.TimeRecorderTracer
import datadog.trace.api.Trace
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.reflect.MethodSignature

class StatsDAspect(private val tracer: TimeRecorderTracer) {

    @Around("@annotation(datadog.trace.api.Trace)")
    fun withTimer(pjp: ProceedingJoinPoint): Any {
        val annotation = (pjp.signature as MethodSignature).method.getAnnotation(Trace::class.java)

        val prefixAux = StatsDTags.get("prefix") ?: ""
        val prefix = if (prefixAux == "") annotation.operationName else prefixAux + "." + annotation.operationName

        return tracer.recordExecutionTime(prefix) { context ->
            pjp.proceed().also {
                StatsDTags.use {
                    context.putAll(StatsDTags.getTags())
                    StatsDTags.addTag("prefix", prefixAux)
                }
            }
        }
    }

}