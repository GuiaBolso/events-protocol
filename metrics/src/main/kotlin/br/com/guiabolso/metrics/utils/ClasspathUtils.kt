package br.com.guiabolso.metrics.utils

object ClasspathUtils {

    fun isNewRelicPresent() = isClassPresent("com.newrelic.api.agent.NewRelic")

    fun isDatadogPresent() = isClassPresent("datadog.opentracing.DDTracer")

    private fun isClassPresent(name: String) = try {
        Class.forName(name)
        true
    } catch (_: ClassNotFoundException) {
        false
    }

}