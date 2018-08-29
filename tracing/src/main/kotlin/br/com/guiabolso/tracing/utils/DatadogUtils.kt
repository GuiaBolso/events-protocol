package br.com.guiabolso.tracing.utils

import datadog.trace.api.DDSpanTypes.WEB_SERVLET
import datadog.trace.api.DDTags.ERROR_MSG
import datadog.trace.api.DDTags.ERROR_STACK
import datadog.trace.api.DDTags.ERROR_TYPE
import datadog.trace.api.DDTags.SPAN_TYPE
import io.opentracing.Span
import io.opentracing.tag.Tags
import io.opentracing.util.GlobalTracer


object DatadogUtils {

    @JvmStatic
    @JvmOverloads
    fun traceAsNewOperation(name: String, type: String = WEB_SERVLET, func: () -> Any) {
        val tracer = GlobalTracer.get()!!
        tracer.buildSpan(name).ignoreActiveSpan().startActive(true).use {
            try {
                it.span().setTag(SPAN_TYPE, type)
                func()
            } catch (e: Exception) {
                notifyError(it.span(), e, false)
            }
        }
    }

    @JvmStatic
    fun <T> traceBlock(name: String, func: () -> T): T {
        val tracer = GlobalTracer.get()!!

        val scope = tracer.buildSpan(name).startActive(true)!!
        return scope.use { _ ->
            try {
                func()
            } catch (e: Exception) {
                notifyError(scope.span(), e, false)
                throw e
            }
        }
    }

    @JvmStatic
    fun notifyError(span: Span, exception: Throwable, expected: Boolean) {
        span.setTag(Tags.ERROR.key, (!expected).toString())
        span.setTag(ERROR_MSG, exception.message ?: "Empty message")
        span.setTag(ERROR_TYPE, exception.javaClass.name)
        span.setTag(ERROR_STACK, ExceptionUtils.getStackTrace(exception))
    }

    @JvmStatic
    fun notifyError(span: Span, message: String, params: Map<String, String?>, expected: Boolean) {
        span.setTag(Tags.ERROR.key, (!expected).toString())
        span.setTag(ERROR_MSG, message)
        params.forEach { entry -> span.setTag(entry.key, entry.value) }
    }

}