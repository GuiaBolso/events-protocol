package br.com.guiabolso.metrics.engine.slf4j

import org.slf4j.MDC
import java.io.Closeable

object MDCCloseable : Closeable {
    override fun close() {
        MDC.clear()
    }
}