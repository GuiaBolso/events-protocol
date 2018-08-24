package br.com.guiabolso.tracing.utils

object ClassPathUtils {

    fun isNewRelicPresent() = isClassPresent("com.newrelic.api.agent.NewRelic")

    fun isDatadogPresent() = isClassPresent("datadog.opentracing.DDTracer")

    private fun isClassPresent(name: String) = try {
        Class.forName(name)
        true
    } catch (_: ClassNotFoundException) {
        false
    }

}