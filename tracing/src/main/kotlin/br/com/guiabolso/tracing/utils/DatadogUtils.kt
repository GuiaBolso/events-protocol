package br.com.guiabolso.tracing.utils

import datadog.trace.api.DDTags.RESOURCE_NAME
import io.opentracing.util.GlobalTracer


object DatadogUtils {

    fun traceOperation(name: String, func: () -> Any) {
        val tracer = GlobalTracer.get()!!

        tracer.buildSpan(name)
                .ignoreActiveSpan()
                .startActive(true)
                .use { scope ->
                    scope.span().setTag(RESOURCE_NAME, name)
                    func()
                }

    }

}