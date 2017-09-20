package br.com.guiabolso.events.utils

import br.com.guiabolso.events.model.Event
import br.com.guiabolso.events.model.EventErrorType
import java.lang.IllegalStateException

object Events {

    @JvmStatic
    fun Event.isSuccess() = this.name.endsWith(":response")

    @JvmStatic
    fun Event.isError() = !this.isSuccess() //TODO("How identify if an event is a error only by its name?")

    @JvmStatic
    fun Event.getErrorType(): EventErrorType {
        if (isSuccess()) throw IllegalStateException("This is not an error event.")
        return EventErrorType.getErrorType(this.name.replaceBeforeLast(":", "").drop(1))
    }

}

