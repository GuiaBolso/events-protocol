package br.com.guiabolso.events

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.SuspendingEventProcessor
import br.com.guiabolso.events.server.exception.handler.EventExceptionHandler
import br.com.guiabolso.events.server.exception.handler.ExceptionHandlerRegistryFactory.exceptionHandler
import br.com.guiabolso.events.server.handler.EventHandler
import br.com.guiabolso.events.server.handler.SimpleEventHandlerRegistry
import br.com.guiabolso.events.tracer.DefaultTracer
import br.com.guiabolso.tracing.Tracer
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.BaseApplicationPlugin
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.application.pluginOrNull
import io.ktor.server.request.contentCharset
import io.ktor.server.request.path
import io.ktor.server.request.receiveChannel
import io.ktor.server.response.respondText
import io.ktor.util.AttributeKey
import io.ktor.util.KtorDsl
import io.ktor.util.toByteArray
import kotlin.reflect.KClass

class Events(configuration: TraceConfiguration) {

    private val eventProcessor = with(configuration) {
        SuspendingEventProcessor(
            registry, exceptionHandler, tracer
        )
    }

    class TraceConfiguration internal constructor(config: TraceConfiguration.() -> Unit) : Configuration() {
        internal lateinit var tracer: Tracer

        init {
            config()
        }

        @KtorDsl
        fun withTracer(tracer: Tracer) {
            this.tracer = tracer
        }
    }

    open class Configuration {
        internal val registry = SimpleEventHandlerRegistry()
        internal val exceptionHandler = exceptionHandler()

        @KtorDsl
        fun event(handler: EventHandler) {
            registry.add(handler)
        }

        @KtorDsl
        fun event(name: String, version: Int, handler: suspend (RequestEvent) -> ResponseEvent) {
            registry.add(name, version, handler)
        }

        @KtorDsl
        inline fun <reified T : Throwable> exception(handler: EventExceptionHandler<T>) =
            exception(T::class) { t, evt, tracer ->
                handler.handleException(t, evt, tracer)
            }

        @KtorDsl
        fun <T : Throwable> exception(
            klass: KClass<T>,
            handler: suspend (exception: T, event: RequestEvent, tracer: Tracer) -> ResponseEvent
        ) = exceptionHandler.register(klass.java) { e, evt, tracer ->
            handler(e, evt, tracer)
        }
    }

    private suspend fun processEvent(rawEvent: String?): String = eventProcessor.processEvent(rawEvent)

    companion object Feature : BaseApplicationPlugin<ApplicationCallPipeline, TraceConfiguration, Events> {
        override val key: AttributeKey<Events> = AttributeKey("Events-Protocol")

        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: TraceConfiguration.() -> Unit
        ): Events {
            val events = Events(TraceConfiguration(configure))

            pipeline.intercept(ApplicationCallPipeline.Call) {
                val path = call.request.path()
                if (path == "/events/" || path == "/events") {
                    val rawEvent =
                        call.receiveChannel().toByteArray().toString(call.request.contentCharset() ?: Charsets.UTF_8)
                    call.respondText(
                        text = events.processEvent(rawEvent), contentType = ContentType.Application.Json
                    )
                    return@intercept finish()
                }
            }
            return events
        }
    }
}

@KtorDsl
fun Application.events(tracer: Tracer = DefaultTracer, configuration: Events.Configuration.() -> Unit) {
    val feature = pluginOrNull(Events)
    if (feature != null) throw IllegalStateException("Cannot initialize Events more than once!")

    val t: Events.TraceConfiguration.() -> Unit = {
        withTracer(tracer)
        configuration()
    }
    install(Events, t)
}
