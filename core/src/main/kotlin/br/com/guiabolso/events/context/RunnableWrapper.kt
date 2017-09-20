package br.com.guiabolso.events.context

class RunnableWrapper(
        private val eventContext: EventContext?,
        private val runnable: Runnable) : Runnable {

    override fun run() {
        EventContextHolder.setContext(eventContext)
        try {
            return runnable.run()
        } finally {
            EventContextHolder.clean()
        }
    }

}