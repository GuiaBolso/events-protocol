package br.com.guiabolso.tracing.engine.slf4j

import br.com.guiabolso.tracing.context.ThreadContextManager
import br.com.guiabolso.tracing.engine.TracerEngine
import java.io.Closeable
import org.slf4j.LoggerFactory
import org.slf4j.MDC

class Slf4JTracer : TracerEngine, ThreadContextManager<MDCContext> {

    override val type = MDCContext::class.java

    override fun setOperationName(name: String) {
        addProperty("Operation", name)
    }

    override fun addProperty(key: String, value: String?) {
        MDC.put(key, value)
    }

    override fun addRootProperty(key: String, value: String?) {
        MDC.put(key, value)
    }

    override fun addProperty(key: String, value: Number?) {
        addProperty(key, value?.toString())
    }

    override fun addRootProperty(key: String, value: Number?) {
        addProperty(key, value?.toString())
    }

    override fun addProperty(key: String, value: Boolean?) {
        MDC.put(key, value?.toString())
    }

    override fun addRootProperty(key: String, value: Boolean?) {
        MDC.put(key, value?.toString())
    }

    override fun addProperty(key: String, value: List<*>) {
        val finalValue: String = value.joinToString(",", "[", "]") {
            "\"${it}\""
        }
        addProperty(key, finalValue)
    }

    override fun <T> recordExecutionTime(name: String, block: (MutableMap<String, String>) -> T): T {
        val start = System.currentTimeMillis()
        val context = mutableMapOf<String, String>()
        try {
            return block(context)
        } finally {
            val elapsedTime = System.currentTimeMillis() - start
            recordExecutionTime(name, elapsedTime, context)
        }
    }

    override fun recordExecutionTime(name: String, elapsedTime: Long, context: Map<String, String>) {
        LOGGER.info("[$name] elapsedTime= $elapsedTime")
        context.forEach { addProperty(it.key, it.value) }
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

    override fun notifyRootError(exception: Throwable, expected: Boolean) {
        notifyError(exception, expected)
    }

    override fun notifyRootError(message: String, params: Map<String, String?>, expected: Boolean) {
        notifyError(message, params, expected)
    }

    override fun clear() {
        MDC.clear()
    }

    override fun extract() = MDCContext(MDC.getCopyOfContextMap() ?: emptyMap())

    override fun withContext(context: MDCContext): Closeable {
        MDC.setContextMap(context.data)
        return Closeable { MDC.clear() }
    }

    private fun toString(map: Map<String, String?>): String {
        return map.entries.joinToString(prefix = "[", postfix = "]") { "${it.key}: ${it.value}" }
    }

    companion object {
        @JvmStatic
        private val LOGGER = LoggerFactory.getLogger(Slf4JTracer::class.java)
    }
}
