package br.com.guiabolso.events.utils

import br.com.guiabolso.events.context.EventContext
import br.com.guiabolso.events.context.EventContextHolder

object EventUtils {

    @JvmStatic
    val eventId: String?
        get() = EventContextHolder.getContext()?.id

    @JvmStatic
    val flowId: String?
        get() = EventContextHolder.getContext()?.flowId

    @JvmStatic
    fun <T> withContext(context: EventContext, func: () -> T): T {
        try {
            EventContextHolder.setContext(context)
            return func()
        } finally {
            EventContextHolder.clean()
        }
    }
}
