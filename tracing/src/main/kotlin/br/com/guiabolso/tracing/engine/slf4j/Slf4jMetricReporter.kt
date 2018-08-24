package br.com.guiabolso.tracing.engine.slf4j

import br.com.guiabolso.tracing.engine.MetricReporterEngine
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.io.Closeable

class Slf4jMetricReporter : MetricReporterEngine<Map<String, String?>> {

    override fun setOperationName(name: String) {
        addProperty("Operation", name)
    }

    override fun addProperty(key: String, value: String?) {
        MDC.put(key, value)
    }

    override fun addProperty(key: String, value: Number?) {
        addProperty(key, value?.toString())
    }

    override fun addProperty(key: String, value: Boolean?) {
        MDC.put(key, value?.toString())
    }

    override fun notifyError(exception: Throwable, expected: Boolean) {
        if (expected) {
            LOGGER.info("[EXPECTED] ${exception.message}", exception)
        } else {
            LOGGER.error(exception.message, exception)
        }
    }

    override fun notifyError(message: String, params: Map<String, String?>, expected: Boolean) {
        if (expected) {
            LOGGER.info("[EXPECTED] $message\t{}", toString(params))
        } else {
            LOGGER.error("$message\t{}", toString(params))
        }
    }

    override fun extractContext(): Map<String, String?> = MDC.getCopyOfContextMap() ?: emptyMap()

    @Suppress("UNCHECKED_CAST")
    override fun withContext(context: Any): Closeable {
        MDC.setContextMap(context as Map<String, String?>)
        return MDCCloseable
    }

    override fun withContext(context: Map<String, String?>, func: () -> Any) {
        withContext(context).use { func() }
    }

    override fun clear() {
        MDC.clear()
    }

    private fun toString(map: Map<String, String?>): String {
        return map.entries.joinToString(prefix = "[", postfix = "]") { "${it.key}: ${it.value}" }
    }

    companion object {
        @JvmStatic
        private val LOGGER = LoggerFactory.getLogger(Slf4jMetricReporter::class.java)
    }

}