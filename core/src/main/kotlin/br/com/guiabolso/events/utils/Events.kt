package br.com.guiabolso.events.utils

import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.ResponseEvent
import java.lang.IllegalStateException

object Events {

    @JvmStatic
    fun ResponseEvent.isSuccess() = this.name.endsWith(":response")

    @JvmStatic
    fun ResponseEvent.isError() = !this.isSuccess()

    @JvmStatic
    fun ResponseEvent.getErrorType(): EventErrorType {
        if (isSuccess()) throw IllegalStateException("This is not an error event.")
        return EventErrorType.getErrorType(this.name.substringAfterLast(":"))
    }

}

