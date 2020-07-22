package br.com.guiabolso.tracing.engine.slf4j

import br.com.guiabolso.tracing.engine.TracerEngine
import java.io.Closeable
import org.slf4j.LoggerFactory
import org.slf4j.MDC

class Slf4JTracer : TracerEngine<Map<String, String?>> {

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

    override fun addProperty(key: String, value: List<*>) {
        val finalValue: String = value.joinToString(",", "[", "]") {
            "\"${it}\""
        }
        addProperty(key, finalValue)
    }

    override fun recordExecutionTime(name: String, elapsedTime: Long, context: MutableMap<String, String>) {
        LOGGER.info("[$name] elapsedTime= $elapsedTime")
        context.forEach { addProperty(it.key, it.value) }
    }

    override fun <T> executeAndRecordTime(name: String, block: (MutableMap<String, String>) -> T): T {
        val start = System.currentTimeMillis()
        val context = mutableMapOf<String, String>()
        try {
            return block(context)
        } finally {
            val elapsedTime = System.currentTimeMillis() - start
            recordExecutionTime(name, elapsedTime, context)
        }
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
        private val LOGGER = LoggerFactory.getLogger(Slf4JTracer::class.java)
    }
}
