package br.com.guiabolso.events.server.exception

import java.io.PrintWriter
import java.io.StringWriter

object ExceptionUtils {

    @JvmStatic
    fun getStackTrace(throwable: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw, true)
        throwable.printStackTrace(pw)
        return sw.buffer.toString()
    }

}