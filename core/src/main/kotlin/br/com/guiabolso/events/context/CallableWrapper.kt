package br.com.guiabolso.events.context

import java.util.concurrent.Callable

class CallableWrapper<T>(
        private val eventContext: EventContext?,
        private val callable: Callable<T>) : Callable<T> {

    override fun call(): T {
        EventContextHolder.setContext(eventContext)
        try {
            return callable.call()
        } finally {
            EventContextHolder.clean()
        }
    }

}