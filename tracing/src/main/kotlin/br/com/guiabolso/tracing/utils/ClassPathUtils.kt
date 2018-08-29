package br.com.guiabolso.tracing.utils

object ClassPathUtils {

    @JvmStatic
    fun isNewRelicPresent() = isClassPresent("com.newrelic.api.agent.NewRelic")

    @JvmStatic
    fun isDatadogPresent() = isClassPresent("datadog.opentracing.DDTracer")

    @JvmStatic
    fun isClassPresent(name: String) = try {
        Class.forName(name)
        true
    } catch (_: ClassNotFoundException) {
        false
    }

}