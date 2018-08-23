package br.com.guiabolso.metrics.engine.newrelic

import com.newrelic.api.agent.Token
import java.io.Closeable

data class CloseableToken(private val token: Token) : Closeable {
    override fun close() {
        token.expire()
    }
}