package br.com.guiabolso.tracing.engine.slf4j

import org.slf4j.MDC
import java.io.Closeable

object MDCCloseable : Closeable {
    override fun close() {
        MDC.clear()
    }
}