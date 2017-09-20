package br.com.guiabolso.events.context

object EventContextHolder {

    private val holder = ThreadLocal<EventContext>()

    fun getContext(): EventContext? = holder.get()

    fun setContext(context: EventContext?) {
        holder.set(context)
    }

    fun clean() {
        holder.remove()
    }

}

