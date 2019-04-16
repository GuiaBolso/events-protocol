package br.com.guiabolso.tracing.aspect

import br.com.guiabolso.tracing.Tracer
import datadog.trace.api.Trace
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature

@Aspect
class StatsDAspect(private val tracer: Tracer) {

    @Around("@annotation(datadog.trace.api.Trace)")
    fun withTimer(pjp: ProceedingJoinPoint): Any {
        val annotation = (pjp.signature as MethodSignature).method.getAnnotation(Trace::class.java)

        val prefixAux = StatsDTags.get("prefix") ?: ""
        val prefix = if (prefixAux == "") annotation.operationName else prefixAux + "." + annotation.operationName
        StatsDTags.addTag("prefix", prefix)

        return tracer.recordExecutionTime(prefix) { context ->
            StatsDTags.use {
                pjp.proceed().also {
                    context.putAll(StatsDTags.getTags())
                    StatsDTags.addTag("prefix", prefixAux)
                }
            }
        }
    }

}