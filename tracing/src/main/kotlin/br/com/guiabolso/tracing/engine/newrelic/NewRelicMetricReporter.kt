package br.com.guiabolso.tracing.engine.newrelic

import br.com.guiabolso.tracing.engine.MetricReporterEngine
import com.newrelic.api.agent.NewRelic
import com.newrelic.api.agent.NewRelic.*
import com.newrelic.api.agent.Token
import java.io.Closeable

class NewRelicMetricReporter : MetricReporterEngine<Token> {

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