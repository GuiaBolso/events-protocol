package br.com.guiabolso.tracing.utils

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
