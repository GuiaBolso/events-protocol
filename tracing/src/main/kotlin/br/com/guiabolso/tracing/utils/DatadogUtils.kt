package br.com.guiabolso.tracing.utils

import io.opentracing.util.GlobalTracer


object DatadogUtils {

    fun traceOperation(name: String, func: () -> Any) {
        val tracer = GlobalTracer.get()!!
        tracer.buildSpan(name)
                .ignoreActiveSpan()
                .startActive(true)
                .use { func() }
    }

}