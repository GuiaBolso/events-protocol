package br.com.guiabolso.metrics.factory

import br.com.guiabolso.metrics.MetricReporter
import br.com.guiabolso.metrics.MetricReporterImpl
import br.com.guiabolso.metrics.async.DefaultAsyncExecutor
import br.com.guiabolso.metrics.async.NewRelicAsyncExecutor
import br.com.guiabolso.metrics.engine.MetricReporterEngine
import br.com.guiabolso.metrics.engine.datadog.DatadogMetricReporter
import br.com.guiabolso.metrics.engine.newrelic.NewRelicMetricReporter
import br.com.guiabolso.metrics.engine.slf4j.Slf4jMetricReporter
import br.com.guiabolso.metrics.utils.ClasspathUtils.isDatadogPresent
import br.com.guiabolso.metrics.utils.ClasspathUtils.isNewRelicPresent
import org.slf4j.LoggerFactory

object MetricReporterFactory {

    @JvmStatic
    private val LOGGER = LoggerFactory.getLogger(MetricReporterFactory::class.java)

    @JvmStatic
    fun createMetricReporter(): MetricReporter = when {
        isDatadogPresent() -> createDatadogMetricReporter()
        isNewRelicPresent() -> createNewRelicMetricReporter()
        else -> createMetricReporterWithoutAPM()
    }

    @JvmStatic
    fun createDatadogMetricReporter(): MetricReporter {
        LOGGER.info("Using Datadog as APM MetricReporter.")
        return MetricReporterImpl(
                compose(Slf4jMetricReporter(), DatadogMetricReporter()),
                DefaultAsyncExecutor()
        )
    }

    @JvmStatic
    fun createNewRelicMetricReporter(): MetricReporter {
        LOGGER.info("Using NewRelic as APM MetricReporter.")
        return MetricReporterImpl(
                compose(Slf4jMetricReporter(), NewRelicMetricReporter()),
                NewRelicAsyncExecutor()
        )
    }

    @JvmStatic
    fun createMetricReporterWithoutAPM(): MetricReporter {
        LOGGER.info("No APM MetricReporter detected.")
        return MetricReporterImpl(
                Slf4jMetricReporter(),
                DefaultAsyncExecutor()
        )
    }

    @JvmStatic
    private fun compose(vararg reporters: MetricReporterEngine<*>) = CompositeMetricReporterEngine(reporters.toList())

}