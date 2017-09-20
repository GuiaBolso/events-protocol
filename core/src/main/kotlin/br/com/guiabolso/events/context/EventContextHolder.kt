package br.com.guiabolso.events.context

import java.util.UUID.randomUUID

object EventContextHolder {

    private val holder = ThreadLocal<EventContext>()

    fun getContext() = holder.get() ?: EventContext(randomUUID().toString(), randomUUID().toString())

    fun setContext(context: EventContext) {
        holder.set(context)
    }

    fun clean() {
        holder.remove()
    }

}

