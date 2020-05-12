package br.com.guiabolso.events.context

object EventContextHolder {

    private val holder = ThreadLocal<EventContext>()

    @JvmStatic
    fun getContext(): EventContext? = holder.get()

    @JvmStatic
    fun setContext(context: EventContext?) {
        holder.set(context)
    }

    @JvmStatic
    fun clean() {
        holder.remove()
    }
}
