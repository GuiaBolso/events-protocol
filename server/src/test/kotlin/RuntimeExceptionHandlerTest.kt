import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.events.exception.EventExceptionHandler
import br.com.guiabolso.events.exception.ExceptionHandlerRegistry
import br.com.guiabolso.events.metric.MDCMetricReporter
import br.com.guiabolso.events.metric.MetricReporter
import br.com.guiabolso.events.model.Event
import br.com.guiabolso.events.model.ResponseEvent
import org.junit.Ignore
import org.junit.Test

class RuntimeExceptionHandlerTest {

    @Test
    @Ignore
    fun name() {
        ExceptionHandlerRegistry.register(RuntimeException::class.java, RuntimeExceptionExceptionHandler())

        val builder = EventBuilder()
        builder.name = "test:event"
        builder.id = "id"
        builder.flowId = "flowId"
        builder.version = 1
        builder.payload = 42
        val event = builder.build()

        ExceptionHandlerRegistry.handleException(RuntimeException("lslas"), event, MDCMetricReporter())
    }
}

class RuntimeExceptionExceptionHandler : EventExceptionHandler<RuntimeException> {
    override fun handleException(exception: RuntimeException, event: Event, metricReporter: MetricReporter): ResponseEvent {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}