package br.com.guiabolso.events

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.EventProcessor
import br.com.guiabolso.events.server.exception.EventExceptionHandler
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistryFactory.exceptionHandler
import br.com.guiabolso.events.server.handler.EventHandler
import br.com.guiabolso.events.server.handler.SimpleEventHandlerRegistry
import br.com.guiabolso.events.tracer.DefaultTracer
import br.com.guiabolso.tracing.Tracer
import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.application.call
import io.ktor.application.featureOrNull
import io.ktor.application.install
import io.ktor.http.ContentType
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.ContextDsl
import kotlin.reflect.KClass

class Events(configuration: TraceConfiguration) {

    private val eventProcessor = with(configuration) {
        EventProcessor(
            registry,
            exceptionHandler,
            tracer
        )
    }

    class TraceConfiguration internal constructor(config: TraceConfiguration.() -> Unit) : Configuration() {
        internal lateinit var tracer: Tracer

        init {
            config()
        }

        @ContextDsl
        fun withTracer(tracer: Tracer) {
            this.tracer = tracer
        }
    }

    open class Configuration {
        internal val registry = SimpleEventHandlerRegistry()
        internal val exceptionHandler = exceptionHandler()

        @ContextDsl
        fun event(handler: EventHandler) {
            registry.add(handler)
        }

        @ContextDsl
        fun event(name: String, version: Int, handler: (RequestEvent) -> ResponseEvent) {
            registry.add(name, version, handler)
        }

        @ContextDsl
        inline fun <reified T : Throwable> exception(handler: EventExceptionHandler<T>) = exception(T::class, handler)

        @ContextDsl
        fun <T : Throwable> exception(klass: KClass<T>, handler: EventExceptionHandler<T>) =
            exceptionHandler.register(klass.java, handler)
    }

    private fun processEvent(rawEvent: String?): String = eventProcessor.processEvent(rawEvent)

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, TraceConfiguration, Events> {
        override val key: AttributeKey<Events> = AttributeKey("Events-Protocol")

        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: TraceConfiguration.() -> Unit
        ): Events {
            val events = Events(TraceConfiguration(configure))

            pipeline.intercept(ApplicationCallPipeline.Call) {
                val path = call.request.path()
                if (path == "/events/") {
                    call.respondText(
                        text = events.processEvent(call.receive()),
                        contentType = ContentType.Application.Json
                    )
                    return@intercept finish()
                }
            }
            return events
        }
    }
}

@ContextDsl
fun Application.events(tracer: Tracer = DefaultTracer, configuration: Events.Configuration.() -> Unit) {
    val feature = featureOrNull(Events)
    if (feature != null) throw IllegalStateException("Cannot initialize Events more than once!")

    val t: Events.TraceConfiguration.() -> Unit = {
        withTracer(tracer)
        configuration()
    }
    install(Events, t)
}
