package br.com.guiabolso.tracing.engine.slf4j

import java.io.Closeable
import org.slf4j.MDC

object MDCCloseable : Closeable {
    override fun close() {
        MDC.clear()
    }
}
