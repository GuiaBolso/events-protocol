package br.com.guiabolso.events

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.EventProcessor
import br.com.guiabolso.events.server.exception.EventExceptionHandler
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistryFactory.exceptionHandler
import br.com.guiabolso.events.server.handler.EventHandler
import br.com.guiabolso.events.server.handler.SimpleEventHandlerRegistry
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

class Events {
    private val registry = SimpleEventHandlerRegistry()
    private val exceptionHandler = exceptionHandler()
    private val eventProcessor = EventProcessor(registry, exceptionHandler)

    @ContextDsl
    fun event(name: String, version: Int, handler: (RequestEvent) -> ResponseEvent) {
        registry.add(name, version, handler)
    }

    @ContextDsl
    fun event(handler: EventHandler) {
        registry.add(handler)
    }

    @ContextDsl
    fun <T : Throwable> exception(clazz: Class<T>, handler: (T, RequestEvent, Tracer) -> ResponseEvent) {
        exceptionHandler.register(clazz, handler)
    }

    @ContextDsl
    fun <T : Throwable> exception(clazz: Class<T>, handler: EventExceptionHandler<T>) {
        exceptionHandler.register(clazz, handler)
    }

    private fun processEvent(rawEvent: String?): String = eventProcessor.processEvent(rawEvent)

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Events, Events> {
        private const val EVENTS_PATH = "/events/"
        override val key: AttributeKey<Events> = AttributeKey("Events-Protocol")

        override fun install(pipeline: ApplicationCallPipeline, configure: Events.() -> Unit): Events {
            val events = Events().apply(configure)

            pipeline.intercept(ApplicationCallPipeline.Call) {
                val path = call.request.path()
                if (path == EVENTS_PATH) {
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
fun Application.events(configuration: Events.() -> Unit): Events =
    featureOrNull(Events)?.apply(configuration) ?: install(Events, configuration)
