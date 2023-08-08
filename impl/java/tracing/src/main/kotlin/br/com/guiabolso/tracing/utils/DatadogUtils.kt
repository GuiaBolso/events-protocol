package br.com.guiabolso.tracing.utils

import datadog.trace.api.DDSpanTypes.HTTP_SERVER
import datadog.trace.api.DDTags.ERROR_MSG
import datadog.trace.api.DDTags.ERROR_STACK
import datadog.trace.api.DDTags.ERROR_TYPE
import datadog.trace.api.DDTags.SPAN_TYPE
import datadog.trace.api.interceptor.MutableSpan
import io.opentracing.Span
import io.opentracing.tag.Tags
import io.opentracing.util.GlobalTracer
import kotlinx.coroutines.runBlocking

object DatadogUtils {

    @JvmStatic
    @JvmOverloads
    fun traceAsNewOperation(
        name: String,
        type: String = HTTP_SERVER,
        onError: suspend (Span, Exception) -> Unit = { span, e ->
            notifyError(span, e, false)
            throw e
        },
        func: () -> Unit
    ) = runBlocking {
        coTraceAsNewOperation(name, type, onError) { func() }
    }

    suspend fun coTraceAsNewOperation(
        name: String,
        type: String = HTTP_SERVER,
        onError: suspend (Span, Exception) -> Unit = { span, e ->
            notifyError(span, e, false)
            throw e
        },
        func: suspend () -> Unit
    ) {
        val tracer = GlobalTracer.get()!!
        val span = tracer.buildSpan(name).ignoreActiveSpan().start()
        tracer.activateSpan(span).use {
            try {
                span.setTag(SPAN_TYPE, type)
                func()
            } catch (e: Exception) {
                onError(span, e)
            } finally {
                span.finish()
            }
        }
    }

    @JvmStatic
    fun <T> traceBlock(name: String, func: () -> T): T = runBlocking {
        suspendingTraceBlock(name) { func() }
    }

    suspend fun <T> suspendingTraceBlock(name: String, func: suspend () -> T): T {
        val tracer = GlobalTracer.get()!!

        val span = tracer.buildSpan(name).start()
        val scope = tracer.activateSpan(span)!!
        return scope.use {
            try {
                func()
            } catch (e: Exception) {
                notifyError(span, e, false)
                throw e
            } finally {
                span.finish()
            }
        }
    }

    @JvmStatic
    fun notifyError(span: Span, exception: Throwable, expected: Boolean) {
        span.setTag(Tags.ERROR.key, !expected)
        span.setTag(ERROR_MSG, exception.message ?: "Empty message")
        span.setTag(ERROR_TYPE, exception.javaClass.name)
        span.setTag(ERROR_STACK, ExceptionUtils.getStackTrace(exception))
    }

    @JvmStatic
    fun notifyError(span: MutableSpan, exception: Throwable, expected: Boolean) {
        span.setTag(Tags.ERROR.key, !expected)
        span.setTag(ERROR_MSG, exception.message ?: "Empty message")
        span.setTag(ERROR_TYPE, exception.javaClass.name)
        span.setTag(ERROR_STACK, ExceptionUtils.getStackTrace(exception))
    }

    @JvmStatic
    fun notifyError(span: Span, message: String, params: Map<String, String?>, expected: Boolean) {
        span.setTag(Tags.ERROR.key, !expected)
        span.setTag(ERROR_MSG, message)
        params.forEach { entry -> span.setTag(entry.key, entry.value) }
    }

    @JvmStatic
    fun notifyError(span: MutableSpan, message: String, params: Map<String, String?>, expected: Boolean) {
        span.setTag(Tags.ERROR.key, !expected)
        span.setTag(ERROR_MSG, message)
        params.forEach { entry -> span.setTag(entry.key, entry.value) }
    }
}
