package br.com.guiabolso.events.utils

import br.com.guiabolso.events.context.EventCoroutineContextForwarder
import br.com.guiabolso.events.context.EventThreadContextManager

object EventUtils {

    @JvmStatic
    val eventId: String?
        get() = EventThreadContextManager.current.id ?: EventCoroutineContextForwarder.current.id

    @JvmStatic
    val flowId: String?
        get() = EventThreadContextManager.current.flowId ?: EventCoroutineContextForwarder.current.flowId
}
