package br.com.guiabolso.tracing.factory

import java.io.Closeable
import org.slf4j.LoggerFactory

data class CompositeTracerEngineCloseable(val context: List<Closeable>) : Closeable {
    override fun close() {
        context.forEach {
            try {
                it.close()
            } catch (e: Exception) {
                log.debug("Error closing trace context", e)
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(CompositeTracerEngineCloseable::class.java)
    }
}
