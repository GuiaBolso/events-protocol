package br.com.guiabolso.tracing.utils

import datadog.trace.api.DDTags
import io.opentracing.tag.Tags
import io.opentracing.util.GlobalTracer


object DatadogUtils {

    fun traceOperation(name: String, func: () -> Any) {
        val tracer = GlobalTracer.get()!!
        tracer.buildSpan(name).ignoreActiveSpan().startActive(true).use {
            try {
                func()
            } catch (e: Exception) {
                tracer.activeSpan()?.apply {
                    setTag(Tags.ERROR.key, true)
                    setTag(DDTags.ERROR_MSG, e.message ?: "Empty message")
                    setTag(DDTags.ERROR_TYPE, e.javaClass.name)
                    setTag(DDTags.ERROR_STACK, ExceptionUtils.getStackTrace(e))
                }
            }
        }
    }

}