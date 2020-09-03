package br.com.guiabolso.tracing.engine.datadog

import com.timgroup.statsd.NonBlockingStatsDClient

class DatadogStatsDTracer(
    prefix: String,
    host: String,
    port: Int
) : DatadogTracer() {

    private val statsDClient = NonBlockingStatsDClient(prefix, host, port)

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

    override fun recordExecutionTime(name: String, elapsedTime: Long, context: MutableMap<String, String>) {
        val tags = context.map { it.key + ":" + it.value }
        statsDClient.recordExecutionTime(name, elapsedTime, 1.0, *tags.toTypedArray())
    }
}
