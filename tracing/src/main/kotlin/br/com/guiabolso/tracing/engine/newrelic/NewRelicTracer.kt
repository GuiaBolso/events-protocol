package br.com.guiabolso.tracing.engine.newrelic

import br.com.guiabolso.tracing.engine.TracerEngine
import com.newrelic.api.agent.NewRelic
import com.newrelic.api.agent.NewRelic.addCustomParameter
import com.newrelic.api.agent.NewRelic.noticeError
import com.newrelic.api.agent.NewRelic.recordResponseTimeMetric
import com.newrelic.api.agent.NewRelic.setTransactionName
import com.newrelic.api.agent.Token
import java.io.Closeable

class NewRelicTracer : TracerEngine<Token> {
    override fun setOperationName(name: String) {
        setTransactionName(null, name)
    }

    override fun addProperty(key: String, value: String?) {
        addCustomParameter(key, value)
    }

    override fun addProperty(key: String, value: Number?) {
        addCustomParameter(key, value)
    }

    override fun addProperty(key: String, value: Boolean?) {
        addCustomParameter(key, value.toString())
    }

    override fun recordExecutionTime(name: String, elapsedTime: Long, context: MutableMap<String, String>) {
        recordResponseTimeMetric(name, elapsedTime)
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
        noticeError(exception, expected)
    }

    override fun notifyError(message: String, params: Map<String, String?>, expected: Boolean) {
        noticeError(message, params, expected)
    }

    override fun extractContext() = NewRelic.getAgent().transaction.token!!

    override fun withContext(context: Any): Closeable {
        (context as Token).link()
        return CloseableToken(context)
    }

    override fun withContext(context: Token, func: () -> Any) {
        withContext(context).use { func() }
    }

    override fun clear() {}

}