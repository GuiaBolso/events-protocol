package br.com.guiabolso.events.utils

import br.com.guiabolso.events.context.EventContext
import br.com.guiabolso.events.context.EventContextHolder

object EventUtils {

    val eventId: String?
        get() = EventContextHolder.getContext()?.id

    val flowId: String?
        get() = EventContextHolder.getContext()?.flowId

    fun <T> withContext(context: EventContext, func: () -> T): T {
        try {
            EventContextHolder.setContext(context)
            return func()
        } finally {
            EventContextHolder.clean()
        }
    }

}